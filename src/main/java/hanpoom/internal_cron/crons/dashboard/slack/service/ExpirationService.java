package hanpoom.internal_cron.crons.dashboard.slack.service;

import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.slack.mapper.ExpirationMapper;
import hanpoom.internal_cron.crons.dashboard.slack.vo.ExpirationVO;
import hanpoom.internal_cron.crons.dashboard.slack.vo.SlackMessageVO;
import hanpoom.internal_cron.utility.slack.enumerate.SlackBot;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ExpirationService {
    private ExpirationMapper expirationMapper;

    public ExpirationService(ExpirationMapper expirationMapper) {
        this.expirationMapper = expirationMapper;
    }

    // 유통기한 손실 알림
    public void reportExpirationManagementLoss(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startMonth = now.minusMonths(0);
        String startDateTime = startMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-01 00:00:00")); // 월 시작 날짜
        String endDateTime = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59")); // 오늘 날짜 기준 바로 전날
        String yesterdayDate = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // 어제 날짜

        System.out.println("확인용 : 시작일-> " + startDateTime + " 종료일-> " + endDateTime + " 어제 날짜-> " + yesterdayDate);

        String yesterdayLoss = expirationMapper.getYesterdayLoss(yesterdayDate); // 전날 발생한 손실 재고
        String yesterdayLossSum = expirationMapper.getYesterdayLossSum(yesterdayDate); // 전날 기준 당일 손실 합계
        String yesterdayMonthLossSum = expirationMapper.getYesterdayMonthLossSum(startDateTime, endDateTime); // 전날 기준 당월 손실 합계

        String message = "";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] 유통기한 손실에 대한 내역입니다.\n", String.valueOf(yesterdayDate)));
        sb.append(String.format("- 손실 재고 : %s 개\n", String.valueOf(yesterdayLoss)));
        sb.append(String.format("- 당일 손실 : $ %s\n", String.valueOf(yesterdayLossSum)));
        sb.append(String.format("- 당월 누적 손실 : $ %s\n", String.valueOf(yesterdayMonthLossSum)));

        message = sb.toString();
        System.out.println("전체 내용 확인용" + message);
        SlackAPI slack = new SlackAPI();

        // 전날 발생한 손실 재고가 0이 아닐 경우에만 실행, 즉 재고가 있을 경우에만 슬랙 알람
        if(!yesterdayLoss.equals(0)){
            try {
                slack.sendMessage(message, SlackBot.Expiration_Loss.getWebHookUrl());
                System.out.println("슬랙 알람 오케이.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("슬랙 알람 전송에 실패했음.");
            }
        }
    }

    // 유통기한 임박 알림
    public void reportExpirationManagementImminent(){
        String message="";
        String slackType="유통기한 임박";
        List<SlackMessageVO> failProductList = new ArrayList<SlackMessageVO>();
        SlackMessageVO slackMessageVO = new SlackMessageVO();
        slackMessageVO.setType(slackType);

        List<String> productIdList = productIdList();
        if(productIdList.size() != 0) {
            List<ExpirationVO> allList = allList(productIdList);
            for (String product : productIdList) {
                System.out.println(product);

                //allList > 해당 상품을 가진 재고 다가져와서 슬랙 알람 보내기
                List<ExpirationVO> temp = allList.stream()
                        .filter(vo -> vo.getProduct_id().equals(product))
                        .collect(Collectors.toList());


                message = slackProductMessage(temp);
                slackMessageVO.setProduct_id(product);
                slackMessageVO.setMessage(message);
                boolean success_flag = slackAPICall(slackMessageVO, failProductList, false);
                if (!success_flag) {
                    productIdList.remove(product);
                }

            }

            if (failProductList.size() != 0) {
                message = slackFailProductMessage(failProductList, slackType);
                slackMessageVO.setMessage(message);
                slackAPICall(slackMessageVO, null, true);
            }

            //mapperdp products 리스트 보내서 update
            expirationMapper.orderStatusUpdate(productIdList);
            expirationMapper.updateToPrivateInAdmin(productIdList);
        }
    }

    //유통기한 만료 알림
   public void reportExpiriedProduct(){
        String message="";
        String slackType="유통기한 종료";
        List<SlackMessageVO> failProductList = new ArrayList<SlackMessageVO>();
        List<ExpirationVO> expiredProductList = expiredProductList();

        SlackMessageVO slackMessageVO = new SlackMessageVO();
        slackMessageVO.setType(slackType);

        //상품id 중복을 제거해준다.
        List<String> productIdList = expiredProductList.stream().map(ExpirationVO::getProduct_id).distinct().collect(Collectors.toList());

        message = expiredSlackMessage(productIdList, expiredProductList);
        slackMessageVO.setMessage(message);
        slackMessageVO.setProduct_ids(productIdList);
        slackAPICall(slackMessageVO, failProductList, false);

        if (failProductList.size() != 0) {
            message = slackFailProductMessage(failProductList,slackType);
            slackMessageVO.setMessage(message);
            slackAPICall(slackMessageVO, null, true);
        }

        expirationMapper.updateToPrivateInAdmin(productIdList);

    }

    private List<String> productIdList(){
        List<String> productList = new ArrayList<>();
        try{
            List<String> operationExpirationProductList = expirationMapper.operationExpirationProduct();
            List<String> stockingExpirationProductList = expirationMapper.stockingExpirationProduct();

            productList.addAll(operationExpirationProductList);
            productList.addAll(stockingExpirationProductList);
            productList= productList.stream().distinct().collect(Collectors.toList());

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return productList;

    }
    private List<ExpirationVO> allList(List<String> products){
        List<ExpirationVO> allList = new ArrayList<>();
        try{
            List<ExpirationVO> operationExpirationAllList = expirationMapper.operationExpiration(products);
            List<ExpirationVO> stockingExpirationAllList = expirationMapper.stockingExpiration(products);

            allList.addAll(operationExpirationAllList);
            allList.addAll(stockingExpirationAllList);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return allList;

    }
    private boolean slackAPICall(SlackMessageVO slackMessageVO,List<SlackMessageVO> failProductList,boolean failCall) {
        boolean success_flag = false;
        SlackAPI slack = new SlackAPI();
        String message= slackMessageVO.getMessage();
        try {
            if(failCall){
                slack.sendMessage(message, SlackBot.ERROR.getWebHookUrl()); // 실패
            }else{
                slack.sendMessage(message, SlackBot.Expiration_Imminent.getWebHookUrl()); // 성공
            }
            System.out.println(slackMessageVO.getType() + " 슬랙 알람 오케이.");
            success_flag = true;
        } catch (Exception e) {
            if(failCall){
                message = slackAPIFailMessage(e.getMessage(), slackMessageVO.getType());
                slack.sendMessage(message, SlackBot.ERROR.getWebHookUrl());
                return false;
            }

            if(slackMessageVO.getProduct_id() == null && slackMessageVO.getProduct_ids() != null){ //유통기한 종료 알림은 상품리스트를 모아서 한번에 슬랙알람을 보낸다.
                for(String productId : slackMessageVO.getProduct_ids()){
                    SlackMessageVO vo = new SlackMessageVO();
                    vo.setProduct_id(productId);
                    vo.setMessage(e.getMessage());
                    failProductList.add(vo);
                }
            }else if(slackMessageVO.getProduct_id() != null){ //유통기한 임박 알림은 상품마다 슬랙알람을 보낸다.
                SlackMessageVO vo = new SlackMessageVO();
                vo.setProduct_id(slackMessageVO.getProduct_id());
                vo.setMessage(e.getMessage());
                failProductList.add(vo);
            }else{ //상품 id 리스트(product_ids)랑 상품id(product_id) 모두 null이다.
                e.printStackTrace();
            }
        }
        return success_flag;
    }

    private Date stringToDate(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date return_date;
        try {
            return_date = formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return return_date;
    }

    private String dateToString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
    private String slackProductMessage(List<ExpirationVO> temp){

        String message = "";
        int total_stock=0;
        Date start_date=null;
        Date end_date=null;

        LocalDateTime now = LocalDateTime.now();
        String Today = now.minusDays(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String exp_date = temp.get(0).getExpiration_date();
        if(exp_date != null) {
//            System.out.println("처음: " + exp_date);
            start_date = stringToDate(exp_date);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] 유통기한 임박 관련 상품 내역입니다.\n", Today));
        sb.append(String.format("- 상품 ID : %s\n", temp.get(0).getProduct_id()));
        sb.append(String.format("- 상품명 : %s\n", temp.get(0).getProduct_name()));
        sb.append("- 위치 \n");
        for(ExpirationVO vo : temp){
            exp_date = vo.getExpiration_date();
            String slackExpDate = exp_date;
            if(vo.getLevel() <= 1){
                total_stock += vo.getAvailable_qty();
            }

            if(exp_date != null) {

                end_date = stringToDate(exp_date);
                boolean compare = end_date.before(start_date);

                if (compare) {
                    start_date = stringToDate(exp_date);
                }
            }else{
                slackExpDate="유통기한 없음";
            }
            sb.append(String.format(" \t\t%s / %s / %s\n",vo.getLocation(), slackExpDate,vo.getAvailable_qty()));
        }
        sb.append(String.format("- 임박재고: %s 개 /", total_stock));
        sb.append(String.format(" %s(최솟값) \n", dateToString(start_date)));
        sb.append("\n[유통기한 임박 관리중] 상태로 변경되었습니다. <@U02H0HU2K1B>\n");
        sb.append("상품 비공개 처리되었습니다. <@U02GK832Y13>\n");
        sb.append("유통기한 실사를 진행해주세요! <@U01S8AEEHM5> <@U013QAHB2MR> <@U01BYAGR1V0>\n");
        sb.append("--------------------------------------------------------------------\n");
        message = sb.toString();
        return message;
    }

    private String slackFailProductMessage(List<SlackMessageVO> failProductList,String slackType){
        String message="";
        message = "<@U01GNBZ4L8Z> <@U03B5VCC68G> " + slackType + " 알람 전송에 실패하였습니다.\n";
        for(SlackMessageVO vo : failProductList) {
            message += "상품 ID : " + vo.getProduct_id();
            message += "에러 메세지 : \n";
            message += vo.getMessage();
            message += "\n\n";
        }

        return message;
    }

    private String slackAPIFailMessage(String error,String slackType){
        String message="";
        message = "<@U01GNBZ4L8Z> <@UU6RURRV4> <@U03B5VCC68G> " + slackType + " 알람 전송에 실패하였습니다.\n";
        message += error;
        return message;
    }

    private List<ExpirationVO> expiredProductList(){
        List<ExpirationVO> productList = new ArrayList<>();
        try{
            List<ExpirationVO> operationExpirationProductList = expirationMapper.operationExpiredProduct();
            List<ExpirationVO> stockingExpirationProductList = expirationMapper.stockingExpiredProduct();

            productList.addAll(operationExpirationProductList);
            productList.addAll(stockingExpirationProductList);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return productList;
    }

    private String expiredSlackMessage(List<String> productIdList, List<ExpirationVO> expiredProductList){


        String message = "";
        StringBuilder sb = new StringBuilder();
        sb.append(expiredSlackMessageHeader());

        for (String product : productIdList) {

            List<ExpirationVO> temp = expiredProductList.stream()
                    .filter(vo -> vo.getProduct_id().equals(product))
                    .collect(Collectors.toList());
            sb.append(expiredSlackMessageBody(temp));
        }

        sb.append(expiredSlackMessageFooter());
        message = sb.toString();
        return message;
    }
    private String expiredSlackMessageHeader(){

        String message = "";
        LocalDateTime now = LocalDateTime.now();
        String Today = now.minusDays(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] 유통기한 만료 상품 알림\n\n", Today));
        message = sb.toString();
        return message;
    }

    private String expiredSlackMessageBody(List<ExpirationVO> temp){

        String message = "";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("- 상품 ID : %s\n", temp.get(0).getProduct_id()));
        sb.append(String.format("- 상품명 : %s\n", temp.get(0).getProduct_name()));
        sb.append("- 위치 \n");
        for(ExpirationVO vo : temp){

            String slackExpDate = vo.getExpiration_date() != null? vo.getExpiration_date(): "유통기한 없음";

            sb.append(String.format(" \t\t%s / %s / %s\n",vo.getLocation(), slackExpDate,vo.getAvailable_qty()));
        }
        sb.append("--------------------------------------------------------------------\n");
        message = sb.toString();
        return message;
    }

    private String expiredSlackMessageFooter(){

        String message = "";
        StringBuilder sb = new StringBuilder();
        sb.append("상품 비공개 처리되었습니다. <@U02GK832Y13>\n");
        sb.append("시트 링크 : https://docs.google.com/spreadsheets/d/1QUQ0K0rbcyXj_BLDf_4AjdOlqPRmufDZph-fWRaT7wg/edit#gid=319893764 <@U01BYAGR1V0>\n");
        message = sb.toString();
        return message;
    }


}


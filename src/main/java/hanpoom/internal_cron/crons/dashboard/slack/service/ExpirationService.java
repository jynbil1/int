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
        List<SlackMessageVO> failProductList = new ArrayList<SlackMessageVO>();
        List<String> productList = productList();
        if(productList.size() != 0) {
            List<ExpirationVO> allList = allList(productList);
            for (String product : productList) {
                System.out.println(product);

                //allList > 해당 상품을 가진 재고 다가져와서 슬랙 알람 보내기
                List<ExpirationVO> temp = allList.stream()
                        .filter(vo -> vo.getProduct_id().equals(product))
                        .collect(Collectors.toList());


                message = slackProductMessage(temp);
                boolean success_flag = slackapicall(message, product, failProductList, false);
                if (!success_flag) {
                    productList.remove(product);
                }

            }

            if (failProductList.size() != 0) {
                message = slackFailProductMessage(failProductList);
                slackapicall(message, null, null, true);
            }

            //mapperdp products 리스트 보내서 update
            expirationMapper.orderStatusUpdate(productList);
            expirationMapper.adminPrivateUpdate(productList);
        }
    }
    private List<String> productList(){
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
    private boolean slackapicall(String message,String product,List<SlackMessageVO> failProductList,boolean failCall) {
        boolean success_flag = false;
        SlackAPI slack = new SlackAPI();
        try {
            if(failCall){
                slack.sendMessage(message, SlackBot.ERROR.getWebHookUrl()); // 실패
            }else{
                slack.sendMessage(message, SlackBot.Expiration_Imminent.getWebHookUrl()); // 성공
            }
            System.out.println("슬랙 알람 오케이.");
            success_flag = true;
        } catch (Exception e) {
            if(failCall){
                message = slackAPIFailMessage(e.getMessage());
                slack.sendMessage(message, SlackBot.ERROR.getWebHookUrl());
                return false;
            }

            SlackMessageVO vo = new SlackMessageVO();
            vo.setProduct_id(product);
            vo.setMessage(e.getMessage());
            failProductList.add(vo);
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

        String exp_date = temp.get(0).getExpiration_date();
        Date start_date = stringToDate(exp_date);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[Today] 유통기한 임박 관련 상품 내역입니다.\n"));
        sb.append(String.format("- 상품 ID : %s\n", temp.get(0).getProduct_id()));
        sb.append(String.format("- 상품명 : %s\n", temp.get(0).getProduct_name()));
        sb.append("- 위치 \n");
        for(ExpirationVO vo : temp){
            sb.append(String.format(" \t\t%s / %s / %s\n",vo.getLocation(), vo.getExpiration_date(),vo.getAvailable_qty()));
            System.out.println(vo.getLevel());
            if(vo.getLevel() <= 1){
                total_stock += vo.getAvailable_qty();
            }

            Date end_date = stringToDate(vo.getExpiration_date());
            int compare = start_date.compareTo(end_date);

            if(compare > 0){
                start_date = stringToDate(vo.getExpiration_date());
            }
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

    private String slackFailProductMessage(List<SlackMessageVO> failProductList){
        String message="";
        message = "<@U01GNBZ4L8Z> <@U03B5VCC68G> 슬랙 알람 전송에 실패하였습니다.\n";
        for(SlackMessageVO vo : failProductList) {
            message += "상품 ID : " + vo.getProduct_id();
            message += "에러 메세지 : \n";
            message += vo.getMessage();
            message += "\n\n";
        }

        return message;
    }

    private String slackAPIFailMessage(String error){
        String message="";
        message = "<@U01GNBZ4L8Z> <@UU6RURRV4> <@U03B5VCC68G> 슬랙 알람 전송에 실패하였습니다.\n";
        message += error;
        return message;
    }


}





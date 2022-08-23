package hanpoom.internal_cron.crons.dashboard.slack.service;


import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.slack.enumerate.Country;
import hanpoom.internal_cron.crons.dashboard.slack.enumerate.ProductSlackType;
import hanpoom.internal_cron.crons.dashboard.slack.mapper.ProductMapper;
import hanpoom.internal_cron.crons.dashboard.slack.vo.ProductVO;
import hanpoom.internal_cron.crons.dashboard.slack.vo.SlackMessageVO;
import hanpoom.internal_cron.utility.slack.enumerate.SlackBot;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private ProductMapper productMapper;
    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;

    }

    public void reportPriceIncorrectProduct(){

        List<ProductVO> productList = productMapper.getUsingProductList();
        System.out.println(productList.size());
        if(productList.size() > 0 ) {
            List<ProductVO> incorrectProductList = incorrectPriceFillter(productList);

            if(!incorrectProductList.isEmpty()) {
                List<ProductVO> koreaProductList = incorrectProductList.stream()
                        .filter(vo -> vo.getCountry().equals("KR"))
                        .collect(Collectors.toList());
                List<ProductVO> usProductList = incorrectProductList.stream()
                        .filter(vo -> vo.getCountry().equals("US"))
                        .collect(Collectors.toList());

                slackAPIAndUpdateAdminStatus(koreaProductList, ProductSlackType.INCORRECT, Country.KOREA);

                slackAPIAndUpdateAdminStatus(usProductList, ProductSlackType.INCORRECT, Country.US);
            }

        }

    }

    public void reportRegularPriceNoneProduct(){

        List<ProductVO> productList = productMapper.getRegularPriceNoneProductList();
        System.out.println(productList.size());
        if(productList.size() > 0 ) {
            slackAPIAndUpdateAdminStatus(productList, ProductSlackType.None, Country.KOREA);
        }

    }
    private List<ProductVO> incorrectPriceFillter(List<ProductVO> productList) {

        Date today = new Date();
        List<ProductVO> incorrectProductList = new ArrayList<>();
        String regularPrice="";
        String salePrice="";
        String price="";
        for(ProductVO product : productList){
            regularPrice=product.getRegular_price();
            salePrice=product.getSale_price();
            price=product.getPrice();
            if(salePrice.equals("")){
                if(regularPrice.equals("") || price.equals("")){
                    incorrectProductList.add(product);
                }else if(Integer.parseInt(regularPrice) != Integer.parseInt(price)){
                    incorrectProductList.add(product);
                }
            }else{
                if(product.getSale_end_datetime() != null){
                    if(!today.before(product.getSale_end_datetime()))
                        salePrice=regularPrice;
                }
                if(salePrice.equals("") || price.equals("")){
                    incorrectProductList.add(product);
                }else if(Integer.parseInt(salePrice) != Integer.parseInt(price)){
                    incorrectProductList.add(product);
                }
            }

            //초기화
            regularPrice="";
            salePrice="";
            price="";
        }
        return incorrectProductList;
    }

    private boolean slackAPIAndUpdateAdminStatus(List<ProductVO> productList, ProductSlackType slackType, Country countryType){
        String message="";
        boolean success_flag=false;
        SlackMessageVO slackMessageVO = new SlackMessageVO();

        // 리스트가 빈 값이 아닐 경우에만 슬랙 알림!
        if(!productList.isEmpty()){
            if(slackType.equals(ProductSlackType.INCORRECT)){
                message = incorrectPriceSlackMessage(productList, countryType.getCountryCode());
            }else if(slackType.equals(ProductSlackType.None)){
                message = nonePriceSlackMessage(productList);
            }

            slackMessageVO.setType(countryType.getCountryCode());
            slackMessageVO.setMessage(message);
            success_flag = slackAPICall(slackMessageVO, false);

            if (!success_flag) {
                slackAPICall(slackMessageVO, true);
            }else{
                List<String> productIdList =  productList.stream().map(ProductVO::getProduct_id).collect(Collectors.toList());
                productMapper.adminPrivateUpdate(productIdList);
            }
        }

        return success_flag;
    }

    private boolean slackAPICall(SlackMessageVO slackMessageVO,boolean failCall) {
        boolean success_flag = false;
        SlackAPI slack = new SlackAPI();
        String message = slackMessageVO.getMessage();
        try {
            if(failCall){
                slack.sendMessage(message, SlackBot.ERROR.getWebHookUrl()); // 실패
            }else{
                slack.sendMessage(message, SlackBot.Price_Error.getWebHookUrl()); // 성공
            }
            System.out.println(" 슬랙 알람 오케이.");
            success_flag = true;
        } catch (Exception e) {
            slackMessageVO.setMessage(slackMessageVO.getType() + " 에러 : " +e.getMessage());;
        }
        return success_flag;
    }

    private String incorrectPriceSlackMessage(List<ProductVO> inCorrectProductList, String country){


        String message = "";
        StringBuilder sb = new StringBuilder();
        sb.append(incorrectPriceSlackMessageHeader(country));
        sb.append(incorrectPriceSlackMessageBody(inCorrectProductList));
        sb.append(incorrectPriceSlackMessageFooter(country));
        message = sb.toString();
        return message;
    }

    private String incorrectPriceSlackMessageHeader(String country){

        String message = "";
        LocalDateTime now = LocalDateTime.now();
        String today = now.minusDays(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("------------------------------[%s]---------------------------------\n",country));
        sb.append(String.format("[%s][%s] 부정확한 가격이 셋팅된 상품 알림\n\n", country, today));
        message = sb.toString();
        return message;
    }

    private String incorrectPriceSlackMessageBody(List<ProductVO> temp){

        String message = "";

        StringBuilder sb = new StringBuilder();
        sb.append("- ID : 상품명 :\n");
        for(ProductVO vo : temp){
            sb.append(String.format("\t%s : %s\n", vo.getProduct_id(),vo.getProduct_name()));
        }
        message = sb.toString();
        return message;
    }

    private String incorrectPriceSlackMessageFooter(String country){

        String message = "";
        StringBuilder sb = new StringBuilder();
        if(country.equals("KR"))
            sb.append("상품 비공개 처리되었습니다. <@U02H0HU2K1B><@U01TMMWKG5P>\n"); //hi , joseph
        else if(country.equals("US"))
            sb.append("상품 비공개 처리되었습니다. <@U01ALLX3K6E>\n"); //bliss
        sb.append("--------------------------------------------------------------------\n");
        message = sb.toString();
        return message;
    }

    private String nonePriceSlackMessage(List<ProductVO> novePriceProductList){


        String message = "";
        StringBuilder sb = new StringBuilder();
        sb.append(nonePriceSlackMessageHeader());
        sb.append(nonePriceSlackMessageBody(novePriceProductList));
        sb.append(nonePriceSlackMessageFooter());
        message = sb.toString();
        return message;
    }

    private String nonePriceSlackMessageHeader(){

        String message = "";
        LocalDateTime now = LocalDateTime.now();
        String today = now.minusDays(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("---------------------------------------------------------------\n"));
        sb.append(String.format("[%s] 정가가 셋팅이 안 된 상품 알림\n\n", today));
        message = sb.toString();
        return message;
    }

    private String nonePriceSlackMessageBody(List<ProductVO> temp){

        String message = "";

        StringBuilder sb = new StringBuilder();
        sb.append("- ID : 상품명 :\n");
        for(ProductVO vo : temp){
            sb.append(String.format("\t%s : %s\n", vo.getProduct_id(),vo.getProduct_name()));
        }
        message = sb.toString();
        return message;
    }

    private String nonePriceSlackMessageFooter(){

        String message = "";
        StringBuilder sb = new StringBuilder();
        sb.append("상품 비공개 처리되었습니다. <@U02H0HU2K1B><@U01TMMWKG5P>\n"); //hi , joseph
        sb.append("--------------------------------------------------------------------\n");
        message = sb.toString();
        return message;
    }


}

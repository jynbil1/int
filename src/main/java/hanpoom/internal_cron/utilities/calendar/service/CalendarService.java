package hanpoom.internal_cron.utilities.calendar.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

@Service
public class CalendarService {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_COMPARE_PATTERN = "yyyyMMdd";
    public static ArrayList<String> holiday = new ArrayList();
    private static final String thisYear = "2021";

    /*
        is_path = true
            /year/month/day
            ex) 2021/09/23
        is_paht = false
            yearmonthday
            ex) 20210923
    
    */
    public String getTodayString(boolean is_path){
        String stoday;
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        if(is_path){
            stoday = "/" + cal.get(Calendar.YEAR) + "/" + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + "/" + String.format("%02d", cal.get(Calendar.DATE));
        }else{
            stoday = Integer.toString(cal.get(Calendar.YEAR)) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE));
        }
        return stoday;
    }


    private boolean is_national_holiday(String year, String date){
        boolean is_national_holiday = false;
        
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            Date nDate = dateFormat.parse(date);
            SimpleDateFormat dateCompareFormat = new SimpleDateFormat(DATE_COMPARE_PATTERN);
            String compareDate = dateCompareFormat.format(nDate);

            if(holiday.isEmpty()){
                System.out.println("api excute");
                get_national_holiday(year);
            }
            if(holiday.contains(compareDate)){
                is_national_holiday = true;
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        return is_national_holiday;
    }

    private void get_national_holiday(String year) {

        if(year == null){
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            year = Integer.toString(cal.get(Calendar.YEAR));
        }
        

        String service_key = "e1WLkR1SnpmdTOnJnegvDQkbPHoX1GMFDUhpUuORp739wu%2BxvPLzlm3pDVKYTMYuSyhdDM6gh8Io47W7pVyNng%3D%3D";
        String apiURL = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
        HttpURLConnection http = null;
        
        try {
            String newURL = apiURL + "?ServiceKey=" + service_key + "&solYear=" + year + "&numOfRows=20";
            // System.out.println("apiurl : " +newURL);
            URL url = new URL(newURL);
            http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            // http.setRequestProperty("Content-Type", "application/json");

            StringBuilder sb = new StringBuilder();
            String output;
            BufferedReader br;

            int responseCode = http.getResponseCode();

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader((http.getInputStream())));

            } else {

                br = new BufferedReader(new InputStreamReader((http.getErrorStream())));
            }

            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new InputSource(new StringReader(sb.toString())));
            document.getDocumentElement().normalize();

            NodeList nodelist = document.getElementsByTagName("locdate");

            for(int i=0; i<nodelist.getLength(); i++){
                Node node = nodelist.item(i);//첫번째 element 얻기

                Node textNode = node.getChildNodes().item(0);

                holiday.add(textNode.getNodeValue());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            http.disconnect();
        }
    }

    private String datePlusMinus(String date, int change, boolean includetime) throws Exception{
        SimpleDateFormat dateFormat;
        if(includetime){
            dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        }else{
            dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        }
        Date nDate = dateFormat.parse(date) ;

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        if(includetime){
            cal.add(Calendar.HOUR, change);
        }else{
            cal.add(Calendar.DATE, change);
        }
        

        String resultDay = dateFormat.format(cal.getTime());

        return resultDay;

    }

    public int getDateDay(String date) throws Exception {
 
     
        String day = "" ;
         
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        Date nDate = dateFormat.parse(date);
         
        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);
         
        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
         
        // switch(dayNum){
        //     case 1:
        //         day = "일";
        //         break ;
        //     case 2:
        //         day = "월";
        //         break ;
        //     case 3:
        //         day = "화";
        //         break ;
        //     case 4:
        //         day = "수";
        //         break ;
        //     case 5:
        //         day = "목";
        //         break ;
        //     case 6:
        //         day = "금";
        //         break ;
        //     case 7:
        //         day = "토";
        //         break ;
                 
        // }
         
         
         
        return dayNum ;
    }
    
    

    public boolean is_holiday(String year, String date){
        
        boolean is_holiday = false;
        
        try{
            int dayNum = getDateDay(date);
            is_holiday = is_national_holiday(year, date);
            
            switch(dayNum){
                case 1:
                    is_holiday = true;
                    break ;
                case 7:
                    is_holiday = true;
                    break ;
            }
            // System.out.println(dayNum);
            // System.out.println(is_holiday);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return is_holiday;
    }

    public String getLatestBusinessDay(String date, boolean afterday, boolean includetime){
        String resultDay = date;
        try{

            while(is_holiday(thisYear,resultDay)){

                if(afterday){
                    resultDay = datePlusMinus(resultDay, 1, includetime);
                }else{
                    resultDay = datePlusMinus(resultDay, -1, includetime);
                }
                
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultDay;
    }

    public int countBusinessDay(String fromDate, String toDate, boolean includetime){
        String resultDay = fromDate;
        int cnt=0;

        try{
            SimpleDateFormat dateFormat;
            if(includetime){
                dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
            }else{
                dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            }
            Date dToDate = dateFormat.parse(toDate);
            long toDateTime = dToDate.getTime();
            // System.out.println("ToDate : " + dToDate);
            
            while(true){
                Date dFromDate = dateFormat.parse(resultDay);
                long fromDateTime = dFromDate.getTime();
                // System.out.println("FromDate : " + dFromDate);
                if(is_holiday(thisYear, resultDay)){
                    cnt++;
                }
                long difftime = (toDateTime - fromDateTime)/1000;
                // System.out.println(difftime);
                if( difftime < 86400 ){
                    System.out.println(difftime +"초 차이");
                    break;
                }
                
                if(includetime){
                    resultDay = datePlusMinus(resultDay, 24, includetime);
                }else{
                    resultDay = datePlusMinus(resultDay, 1, includetime);
                }
                

            }
            System.out.println("count : " + cnt);
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return cnt;
    }

    public long countBusinessSecond(String fromDate, String toDate){
        String resultDay = fromDate;
        long hourbysecond = 0;
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
            Date dToDate = dateFormat.parse(toDate);
            long toDateTime = dToDate.getTime();
            // System.out.println("ToDate : " + dToDate);
        
            while(true){
                Date dFromDate = dateFormat.parse(resultDay);
                long fromDateTime = dFromDate.getTime();
                Calendar cal = Calendar.getInstance();
                cal.setTime(dFromDate);
                // System.out.println("FromDate : " + dFromDate);
                if(!is_holiday(thisYear, resultDay)){
                    long timebysecond =  cal.get(Calendar.HOUR)*3600
                                    + cal.get(Calendar.MINUTE)*60
                                    + cal.get(Calendar.SECOND);
                    hourbysecond += timebysecond;
                }
                long difftime = (toDateTime - fromDateTime)/1000;
                // System.out.println(difftime);
                if( difftime < 86400 ){
                    System.out.println(difftime +"초 차이");
                    hourbysecond += difftime;
                    break;
                }
                
                resultDay = datePlusMinus(resultDay, 24, true);
               
                

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return hourbysecond;
    }

    public String getNextMonthFirstDay(String date){
        String resultDay ="";
        try{
            //다음달셋팅
            String nextMonthDay = datePlusMinus(date, 30, false);
            //해당달 첫날
            resultDay = nextMonthDay.substring(0, 8) + "01";
            
            resultDay = getLatestBusinessDay(resultDay, true, false);

        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultDay;
    }

    public String getNextMonthLastDay(String date){
        String resultDay = "";
        try{
            //다음달셋팅
            String nextMonthDay = datePlusMinus(date, 30, false);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            Date dNextMonthDay = dateFormat.parse(nextMonthDay);

            Calendar cal = Calendar.getInstance();
            cal.setTime(dNextMonthDay);
            String lastday = Integer.toString(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            resultDay = nextMonthDay.substring(0, 8) + lastday;

            resultDay = getLatestBusinessDay(resultDay, false, false);

        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultDay;
    }

    public String getBusinessDayLater(String date, int laterCount){
        String resultDay = date;
        int cnt=0;
        try{
            
            while(true){

                if(!is_holiday(thisYear, resultDay)){
                    cnt++;
                }
                // System.out.println(difftime);
                if( cnt == laterCount ){
                    break;
                }
                
                resultDay = datePlusMinus(resultDay, 1, false);

            }
            System.out.println("count : " + cnt);
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultDay;
    }
    

}

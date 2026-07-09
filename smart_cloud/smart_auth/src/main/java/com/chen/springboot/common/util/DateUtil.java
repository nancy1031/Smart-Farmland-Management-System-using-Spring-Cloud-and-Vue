package com.chen.springboot.common.util;

import org.junit.Test;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class DateUtil {

    public static String date1_format = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取几天后的时间
     * @param day
     * @return
     */
    public static Date getDayAfterTime(int day){
        Calendar after = Calendar.getInstance();
        after.add(Calendar.DAY_OF_MONTH, + day);
        return after.getTime();
    }


    /**
     * 获取几天前时间
     * @param day
     * @return
     */
    public static Date getDayBeforeTime(int day){
        Calendar after = Calendar.getInstance();
        after.add(Calendar.DAY_OF_MONTH, - day);
        return after.getTime();
    }
    
    public static Date getDayAfterTime(Date date,int day){
    	Calendar after = Calendar.getInstance();
    	after.setTime(date);
        int startDate=after.get(Calendar.DATE);
        after.set(Calendar.DATE,startDate+day);
        return after.getTime();
    }

    public static Date stringToDate(String strDate, String format) throws ParseException {
        if(strDate == null) {
            return null;
        }
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(strDate);
    }

    public static String formatterDate(Date date) {
        if (date == null){
            date = new Date();
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String formatterDate(Date date, String format) {
        if (format.isEmpty()){
            format = "yyyyMMddHHmmss";
        }

        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    @Test
    public void test01(){
        Date dayBeforeDate = DateUtil.getDayBeforeTime(60);
        System.out.println(dayBeforeDate);

        String dateStrLimit = DateUtil.formatterDate(dayBeforeDate);
        System.out.println(dateStrLimit);

        System.out.println("```````````");
        Date date = new Date();
        String s = DateUtil.formatterDate(date, "yyyy-MM-dd");
        System.out.println(date);
        System.out.println(s);

    }

}

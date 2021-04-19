package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>
 *  时间日期工具类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/15 17:37
 */
@Slf4j
public class DateTimeUtil {

    static String USFormat = "EEE MMM dd HH:mm:ss Z yyyy";

    static String CNFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前时间至第二天凌晨的秒数
     * @return ：当前时间至第二天凌晨的秒数
     */
    public static int getSeconds(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int)(cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 时间格式转换
     * @param dateStr 时间字符串(EEE MMM dd HH:mm:ss zzz yyyy)
     * @return 常用时间字符串
     */
    public static String dateStrConver(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat(USFormat, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Central "));
        try{
            Date date = sdf.parse(dateStr);
            SimpleDateFormat cnFormat = new SimpleDateFormat(CNFormat);
            cnFormat.setTimeZone(TimeZone.getTimeZone( "GMT-8:00 "));
            dateStr = cnFormat.format(date);
        }catch (Exception e){
            log.info("时间格式转化异常");
            e.printStackTrace();
        }
        return dateStr;
    }


}

package com.example.demo.util;

import java.util.Calendar;

/**
 * <p>
 *  时间日期工具类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/15 17:37
 */
public class DateTimeUtil {

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

}

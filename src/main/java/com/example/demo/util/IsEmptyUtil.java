package com.example.demo.util;

import java.util.List;

/**
 * <p>
 *  判断对象是否为空
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/9 14:16
 */
public class IsEmptyUtil {

    /**
     * 判断对象为空
     *
     * @param obj
     *      对象名
     * @return 是否为空
     */
    // @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }else if ((obj instanceof List)) {
            return ((List) obj).size() == 0;
        }else if ((obj instanceof String)) {
            return ((String) obj).trim().equals("");
        }
        return false;
    }

    /**
     * 判断对象不为空
     *
     * @param obj
     *      对象名
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}

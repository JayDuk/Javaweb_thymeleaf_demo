package com.mvc.util;

/**
 * @author javaok
 * 2022/11/29 16:47
 */
public class ParamBuilder {
    public static Object build(String value, Object paramType) {
        if (value == null) {
            return null;
        }

        Class<?> type = (Class<?>) paramType;
        if (String.class.equals(type)) {
            return value;
        } else if (Integer.class.equals(type)) {
            return Integer.parseInt(value);
        } else if (Double.class.equals(type)) {
            return Double.parseDouble(value);
        } else if (Float.class.equals(type)) {
            return Float.parseFloat(value);
        }


        return null;
    }
}

package com.dasheng.utils;

public class StringUtils {

    public static boolean isNotNull(Object o) {
        return o != null;
    }

    public static boolean isNull(Object o) {
        return !isNotNull(o);
    }

    public static boolean isNullOrEmpty(Object str) {
        return isNull(str) || str.toString().trim().length() == 0;
    }

    public static boolean hasText(String str) {
        return str != null && !"".equals(str.trim());
    }

    public static String safeString(Object obj, String defVal) {
        String strVal = defVal;
        if (!isNullOrEmpty(obj)) {
            try {
                strVal = String.valueOf(obj);
            } catch (Exception var4) {
                ;
            }
        }

        return strVal;
    }

    public static String safeString(Object obj) {
        return safeString(obj, "");
    }

    public static int safeInt(Object obj, int defVal) {
        int intVal = defVal;
        if (!isNullOrEmpty(obj)) {
            try {
                intVal = Integer.parseInt(String.valueOf(obj));
            } catch (Exception var4) {
                ;
            }
        }

        return intVal;
    }

    public static int safeInt(Object obj) {
        return safeInt(obj, 0);
    }
}

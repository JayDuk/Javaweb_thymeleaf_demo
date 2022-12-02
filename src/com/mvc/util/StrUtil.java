package com.mvc.util;

/**
 * @author javaok
 * 2022/11/30 10:49
 */
public class StrUtil {
    public static final String REDIRECT = "redirect:";
    public static final String FORWARD = "forward:";
    final static String UNDERLINE = "_";

    public static String transToLowerCamel(String fileName) {
        if (fileName == null) {
            return null;
        }
        String[] s = fileName.split(UNDERLINE);
        if (s.length == 1) {
            return fileName;
        }
        StringBuilder ret = new StringBuilder(s[0]);
        for (int i = 1; i < s.length; i++) {
            ret.append(Character.toUpperCase(s[i].charAt(0)));
            ret.append(s[i].substring(1));
        }
        return ret.toString();
    }
}

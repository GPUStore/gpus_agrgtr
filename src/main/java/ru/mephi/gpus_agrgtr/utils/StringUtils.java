package ru.mephi.gpus_agrgtr.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String getByPattern(String pattern, String text) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        return sb.toString();
    }

}

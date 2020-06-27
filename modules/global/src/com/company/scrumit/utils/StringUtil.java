package com.company.scrumit.utils;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(StringUtil.NAME)
public class StringUtil {
    public static final String NAME = "scrumit_StringUtil";

    public String getEmailFromString(String source){
        if (source == null)
            return null;
        Pattern pattern = Pattern.compile("[a-z0-9_.-]+@[a-z0-9_.-]+");
        Matcher matcher = pattern.matcher(source);
        if (matcher.find())
            return  matcher.group(0);
        else
            return null;
    }

    public  String formatDurationToString(Duration duration){
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        StringBuilder result = new StringBuilder();
        if (days != 0)
            result.append(days + "д. ");
        if (hours != 0)
            result.append(hours + "ч. ");
        if (minutes != 0)
            result.append(minutes + "м.");
        if (result.length() == 0)
            result.append("0м.");
        return result.toString();
    }
}
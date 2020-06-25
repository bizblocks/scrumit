package com.company.scrumit.core;

import org.springframework.stereotype.Component;

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
}
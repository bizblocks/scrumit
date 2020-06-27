package com.company.scrumit.utils;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component(DateUtil.NAME)
public class DateUtil {
    public static final String NAME = "scrumit_DateUtil";

    public Duration getDurationBetweenTwoDates(Date begin, Date end){
        long durationMilis = end.getTime() - begin.getTime();
        return  Duration.ofMillis(durationMilis);
    }
}
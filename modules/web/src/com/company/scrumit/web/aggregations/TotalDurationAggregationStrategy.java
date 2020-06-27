package com.company.scrumit.web.aggregations;

import com.company.scrumit.utils.StringUtil;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;

import java.time.Duration;
import java.util.Collection;

public class TotalDurationAggregationStrategy implements AggregationStrategy<Long, String> {

    StringUtil stringUtil = AppBeans.get(StringUtil.NAME);

    @Override
    public String aggregate(Collection<Long> propertyValues) {
        long totalDurationMilis = 0L;
            for (Long durationMilis : propertyValues){
                totalDurationMilis+=durationMilis;
            }
        return stringUtil.formatDurationToString(Duration.ofMillis(totalDurationMilis));
    }



    @Override
    public Class<String> getResultClass() {
        return String.class;
    }
}

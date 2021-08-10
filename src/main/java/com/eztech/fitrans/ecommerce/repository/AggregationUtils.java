package com.eztech.fitrans.ecommerce.repository;

import org.springframework.data.util.Pair;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import static com.eztech.fitrans.ecommerce.Constants.DAYS_IN_WEEK;

public class AggregationUtils {
    private AggregationUtils() {
    }

    public static <T> Map<Integer, T> generateDefaultMap(int size, T defaultValue) {
        return generateDefaultMap(1, size, defaultValue);
    }

    public static <T> Map<Integer, T> generateDefaultMap(int startIndex, int endIndex, T defaultValue) {
        Map<Integer, T> defaultMap = new TreeMap<>();
        for (int i = startIndex; i <= endIndex; i++) {
            defaultMap.put(i, defaultValue);
        }
        return defaultMap;
    }

    public static Pair<LocalDateTime, LocalDateTime> getWeekInterval(LocalDateTime dateTime) {
        while (dateTime.getDayOfWeek() != DayOfWeek.MONDAY) {
            dateTime = dateTime.minusDays(1);
        }

        LocalDateTime startWeek = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(),
                dateTime.getDayOfMonth(), 0, 1).minusDays(1); //in sql week starts from Sunday
        LocalDateTime endWeek = startWeek.plusDays(DAYS_IN_WEEK);

        return Pair.of(startWeek, endWeek);
    }
}

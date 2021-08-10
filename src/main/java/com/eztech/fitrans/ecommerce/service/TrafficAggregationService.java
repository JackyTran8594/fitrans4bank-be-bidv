package com.eztech.fitrans.ecommerce.service;

import com.eztech.fitrans.ecommerce.DTO.TrafficAggregationDTO;
import com.eztech.fitrans.ecommerce.DTO.TrafficComparisonDTO;
import com.eztech.fitrans.ecommerce.entity.enums.AggregationEnum;
import com.eztech.fitrans.ecommerce.entity.enums.MonthsEnum;
import com.eztech.fitrans.ecommerce.entity.enums.WeekDaysEnum;
import com.eztech.fitrans.ecommerce.repository.AggregationUtils;
import com.eztech.fitrans.ecommerce.repository.CustomTrafficAggregationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.eztech.fitrans.ecommerce.Constants.DAYS_IN_WEEK;
import static com.eztech.fitrans.ecommerce.Constants.YEARS_BEFORE;
import static com.eztech.fitrans.ecommerce.Constants.LAST_MINUTE;
import static com.eztech.fitrans.ecommerce.Constants.LAST_HOUR;
import static com.eztech.fitrans.ecommerce.Constants.DAYS_IN_DECEMBER;
import static com.eztech.fitrans.ecommerce.Constants.AMOUNT_OF_PROCENTS;

@Service
public class TrafficAggregationService {

    private CustomTrafficAggregationRepository customTrafficAggregationRepository;

    @Autowired
    public TrafficAggregationService(CustomTrafficAggregationRepository customTrafficAggregationRepository) {
        this.customTrafficAggregationRepository = customTrafficAggregationRepository;
    }

    private List<TrafficAggregationDTO> convertToTrafficAggregationDTO(Map<Integer, Long> resultMap,
                                                   Function<Map.Entry<Integer, Long>, String> func) {
        return  resultMap.entrySet().stream().map(entry -> new TrafficAggregationDTO(func.apply(entry),
                        entry.getValue().intValue(), 0, null)).collect(Collectors.toList());
    }

    public List<TrafficAggregationDTO> getDataForTable(String filter) {
        AggregationEnum aggregationParameter = AggregationEnum.valueOf(filter.toUpperCase());
        LocalDateTime startDate, endDate;
        List<TrafficAggregationDTO> result;
        Map<Integer, Long> resultMap;

        Function<Map.Entry<Integer, Long>, String> entryFunction;
        switch (aggregationParameter) {
            case YEAR:
                startDate = LocalDateTime.now().minusYears(YEARS_BEFORE);
                endDate = LocalDateTime.now();

                entryFunction = entry -> entry.getKey().toString();
                break;
            case MONTH:
                int yearBefore = LocalDateTime.now().minusYears(1).getYear();
                startDate = LocalDateTime.of(
                        yearBefore, Month.JANUARY, 1, 0, 1
                );
                endDate = LocalDateTime.of(
                        yearBefore, Month.DECEMBER, DAYS_IN_DECEMBER, LAST_HOUR, LAST_MINUTE);

                entryFunction = entry -> MonthsEnum.from(entry.getKey());
                break;
            case WEEK:
                LocalDateTime randomDayOfWeek = LocalDateTime.now().minusDays(DAYS_IN_WEEK);
                Pair<LocalDateTime, LocalDateTime> weekInterval = AggregationUtils.getWeekInterval(randomDayOfWeek);
                startDate = weekInterval.getFirst();
                endDate = weekInterval.getSecond();

                entryFunction = entry -> WeekDaysEnum.from(entry.getKey());
                break;
            default:
                throw new IllegalArgumentException("Wrong aggregation");
        }

        resultMap = customTrafficAggregationRepository
                .getDataByPeriod(startDate, endDate, aggregationParameter);
        result = convertToTrafficAggregationDTO(resultMap, entryFunction);


        if (result == null || result.size() <= 1) {
            return result;
        }

        //delete entries with empty value
        Iterator<TrafficAggregationDTO> it = result.iterator();
        while (it.next().getValue() == 0) {
            it.remove();
        }

        for (int i = 1; i < result.size(); i++) {

            int previousValue = result.get(i - 1).getValue();
            String previousPeriod = result.get(i - 1).getPeriod();
            int currentValue = result.get(i).getValue();
            TrafficAggregationDTO currentElement = result.get(i);

            double currentTrend = previousValue == 0 ? 0 :
                    ((double) (currentValue - previousValue) / previousValue) * AMOUNT_OF_PROCENTS;
            currentElement.setTrend(currentTrend);
            currentElement.setComparison(new TrafficComparisonDTO(previousPeriod, previousValue,
                                                                    currentElement.getPeriod(), currentValue));
        }

        return result;
    }
}

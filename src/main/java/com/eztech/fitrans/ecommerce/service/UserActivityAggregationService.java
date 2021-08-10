package com.eztech.fitrans.ecommerce.service;

import com.eztech.fitrans.ecommerce.DTO.UserActivityDTO;
import com.eztech.fitrans.ecommerce.entity.enums.AggregationEnum;
import com.eztech.fitrans.ecommerce.entity.enums.MonthsEnum;
import com.eztech.fitrans.ecommerce.entity.enums.WeekDaysEnum;
import com.eztech.fitrans.ecommerce.repository.AggregationUtils;
import com.eztech.fitrans.ecommerce.repository.CustomUserActivitiesAggregationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.eztech.fitrans.ecommerce.Constants.AMOUNT_OF_PROCENTS;
import static com.eztech.fitrans.ecommerce.Constants.DAYS_IN_WEEK;
import static com.eztech.fitrans.ecommerce.Constants.YEARS_BEFORE;

@Service
public class UserActivityAggregationService {

    private CustomUserActivitiesAggregationRepository customUserActivitiesAggregationRepository;

    @Autowired
    public UserActivityAggregationService(CustomUserActivitiesAggregationRepository
                                                  customUserActivitiesAggregationRepository) {
        this.customUserActivitiesAggregationRepository = customUserActivitiesAggregationRepository;
    }

    private List<UserActivityDTO> convertToTrafficUserActivityDTO(Map<Integer, Long> resultMap,
                                                         Function<Map.Entry<Integer, Long>, String> labelFunction) {
        return resultMap.entrySet().stream().map(entry -> {
            String label = labelFunction.apply(entry);
            int count = entry.getValue().intValue();
            return new UserActivityDTO(label, count, 0);
        }).collect(Collectors.toList());
    }


    public List<UserActivityDTO> getDataForTable(String aggregation) {
        AggregationEnum aggregationParameter = AggregationEnum.valueOf(aggregation.toUpperCase());
        LocalDateTime startDate, endDate;
        List<UserActivityDTO> result;
        Map<Integer, Long> resultMap;
        Function<Map.Entry<Integer, Long>, String> labelFunction;
        switch (aggregationParameter) {
            case YEAR:
                startDate = LocalDateTime.now().minusYears(YEARS_BEFORE);
                endDate = LocalDateTime.now();

                labelFunction = entry -> entry.getKey().toString();
                break;
            case MONTH:
                LocalDateTime monthBefore = LocalDateTime.now().minusMonths(1);
                startDate = LocalDateTime.of(monthBefore.getYear(), monthBefore.getMonth(), 1, 0, 1);
                endDate = startDate.plusMonths(1);

                labelFunction = entry -> entry.getKey() + " " + MonthsEnum.from(startDate.getMonthValue());
                break;
            case WEEK:
                LocalDateTime randomDayOfWeek = LocalDateTime.now().minusDays(DAYS_IN_WEEK);
                Pair<LocalDateTime, LocalDateTime> weekInterval = AggregationUtils.getWeekInterval(randomDayOfWeek);
                startDate = weekInterval.getFirst();
                endDate = weekInterval.getSecond();

                labelFunction = entry -> WeekDaysEnum.from(entry.getKey());
                break;
            default:
                throw new IllegalArgumentException("Wrong aggregation");
        }

        resultMap = customUserActivitiesAggregationRepository
                .getDataByPeriod(startDate, endDate, aggregationParameter);

        result = convertToTrafficUserActivityDTO(resultMap, labelFunction);

        if (result != null && result.size() > 1) {
            for (int i = 1; i < result.size(); i++) {
                int previousValue = result.get(i - 1).getPagesVisit();
                int currentValue = result.get(i).getPagesVisit();

                result.get(i).setTrend(previousValue == 0 ? 0 :
                        ((double) (currentValue - previousValue) / previousValue) * AMOUNT_OF_PROCENTS);
            }
        }

        return result;
    }
}

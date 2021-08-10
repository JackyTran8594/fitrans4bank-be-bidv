package com.eztech.fitrans.ecommerce.service;


import com.eztech.fitrans.ecommerce.DTO.BaseChartDTO;
import com.eztech.fitrans.ecommerce.DTO.ChartDataDTO;
import com.eztech.fitrans.ecommerce.DTO.OrdersProfitDTO;
import com.eztech.fitrans.ecommerce.DTO.OrdersSummaryDTO;
import com.eztech.fitrans.ecommerce.DTO.OrderTypeStatisticDTO;
import com.eztech.fitrans.ecommerce.DTO.ProfitForTwoMonthChartDTO;
import com.eztech.fitrans.ecommerce.DTO.ChartAdditionalInfoDTO;
import com.eztech.fitrans.ecommerce.entity.statistic.StatisticUnit;
import com.eztech.fitrans.ecommerce.entity.AggregatedData;
import com.eztech.fitrans.ecommerce.entity.enums.AggregationEnum;
import com.eztech.fitrans.ecommerce.entity.enums.MonthsEnum;
import com.eztech.fitrans.ecommerce.entity.enums.OrderStatusEnum;
import com.eztech.fitrans.ecommerce.entity.enums.WeekDaysEnum;
import com.eztech.fitrans.ecommerce.entity.statistic.OrderTypeStatistic;
import com.eztech.fitrans.ecommerce.entity.statistic.OrdersProfit;
import com.eztech.fitrans.ecommerce.entity.statistic.OrdersSummary;
import com.eztech.fitrans.ecommerce.repository.AggregationUtils;
import com.eztech.fitrans.ecommerce.repository.CustomOrderAggregatedRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import static com.eztech.fitrans.ecommerce.Constants.DAYS_IN_WEEK;
import static com.eztech.fitrans.ecommerce.Constants.LAST_MINUTE;
import static com.eztech.fitrans.ecommerce.Constants.LAST_HOUR;
import static com.eztech.fitrans.ecommerce.Constants.DAYS_IN_DECEMBER;
import static com.eztech.fitrans.ecommerce.Constants.YEARS_BEFORE;
import static com.eztech.fitrans.ecommerce.Constants.AMOUNT_OF_PROCENTS;
import static com.eztech.fitrans.ecommerce.Constants.SECONDS_IN_MINUTE;

@Service
public class OrderAggregationService {
    private CustomOrderAggregatedRepository customOrderAggregatedRepository;
    private ModelMapper modelMapper;

    @Autowired
    public OrderAggregationService(CustomOrderAggregatedRepository customOrderAggregatedRepository,
                                   ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.customOrderAggregatedRepository = customOrderAggregatedRepository;
    }

    private List<Long> calculateAllStatusesForCount(List<Long> paymentValues, List<Long> cancelledValues) {
        List<Long> allStatuses = new ArrayList<>();
        for (int i = 0; i < paymentValues.size(); i++) {
            allStatuses.add(paymentValues.get(i) + cancelledValues.get(i));
        }
        return allStatuses;
    }

    private List<Double> calculateAllStatusesForProfit(List<Double> paymentValues, List<Double> cancelledValues) {
        List<Double> allStatuses = new ArrayList<>();
        for (int i = 0; i < paymentValues.size(); i++) {
            allStatuses.add(paymentValues.get(i) + cancelledValues.get(i));
        }
        return allStatuses;
    }

    private List<String> getAxisLabelsByAggregation(Set<Integer> values, AggregationEnum aggregation) {
        List<String> labels;
        switch (aggregation) {
            case WEEK:
                labels = Arrays.asList(WeekDaysEnum.names());
                break;
            case MONTH:
                labels = Arrays.asList(MonthsEnum.names());
                break;
            case YEAR:
                labels = values.stream()
                        .map(val -> Integer.toString(val))
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalArgumentException("Wrong aggregation");
        }
        return labels;
    }

    public BaseChartDTO<Long> getCountDataForChart(String aggregation) {
        BaseChartDTO<Long> stats = new BaseChartDTO<>();
        List<ChartDataDTO<Long>> lines = new ArrayList<>();

        AggregationEnum aggregationParameter = AggregationEnum.valueOf(aggregation.toUpperCase());

        LocalDateTime startDate, endDate;
        switch (aggregationParameter) {
            case WEEK:
                LocalDateTime randomDayOfWeek = LocalDateTime.now().minusDays(DAYS_IN_WEEK);
                Pair<LocalDateTime, LocalDateTime> weekInterval = AggregationUtils.getWeekInterval(randomDayOfWeek);
                startDate = weekInterval.getFirst();
                endDate = weekInterval.getSecond();
                break;
            case MONTH:
                int yearBefore = LocalDateTime.now().minusYears(1).getYear();
                startDate = LocalDateTime.of(
                        yearBefore, Month.JANUARY, 1, 1, 1);
                endDate = LocalDateTime.of(
                        yearBefore, Month.DECEMBER, DAYS_IN_DECEMBER, LAST_HOUR, LAST_MINUTE);
                break;
            case YEAR:
                startDate = LocalDateTime.now().minusYears(YEARS_BEFORE);
                endDate = LocalDateTime.now();
                break;
            default:
                throw new IllegalArgumentException("Wrong aggregation");
        }

        Map<Integer, Long> paymentStatusAggr = customOrderAggregatedRepository
                .getCountDataForPeriod(startDate, endDate, OrderStatusEnum.PAYMENT, aggregationParameter);
        Map<Integer, Long> cancelledStatusAggr = customOrderAggregatedRepository
                .getCountDataForPeriod(startDate, endDate, OrderStatusEnum.CANCELLED, aggregationParameter);

        List<Long> paymentValues = new ArrayList<>(paymentStatusAggr.values());
        List<Long> cancelledValues = new ArrayList<>(cancelledStatusAggr.values());
        List<Long> allValues = calculateAllStatusesForCount(paymentValues, cancelledValues);

        List<String> axisXLabels = getAxisLabelsByAggregation(paymentStatusAggr.keySet(), aggregationParameter);

        lines.add(new ChartDataDTO<>(paymentValues, "Payment"));
        lines.add(new ChartDataDTO<>(cancelledValues, "Cancelled"));
        lines.add(new ChartDataDTO<>(allValues, "All"));

        stats.setAxisXLabels(axisXLabels);

        stats.setLines(lines);

        return stats;
    }

    public BaseChartDTO<Double> getProfitDataForChart(String aggregation) {
        BaseChartDTO<Double> stats = new BaseChartDTO<>();
        List<ChartDataDTO<Double>> lines = new ArrayList<>();

        AggregationEnum aggregationParameter = AggregationEnum.valueOf(aggregation.toUpperCase());

        LocalDateTime startDate, endDate;
        switch (aggregationParameter) {
            case WEEK:
                LocalDateTime randomDayOfWeek = LocalDateTime.now().minusDays(DAYS_IN_WEEK);
                Pair<LocalDateTime, LocalDateTime> weekInterval = AggregationUtils.getWeekInterval(randomDayOfWeek);
                startDate = weekInterval.getFirst();
                endDate = weekInterval.getSecond();
                break;
            case MONTH:
                int yearBefore = LocalDateTime.now().minusYears(1).getYear();
                startDate = LocalDateTime.of(
                        yearBefore, Month.JANUARY, 1, 0, 1);
                endDate = LocalDateTime.of(
                        yearBefore, Month.DECEMBER, DAYS_IN_DECEMBER, LAST_HOUR, LAST_MINUTE);
                break;
            case YEAR:
                startDate = LocalDateTime.now().minusYears(YEARS_BEFORE);
                endDate = LocalDateTime.now();
                break;
            default:
                throw new IllegalArgumentException("Wrong aggregation");
        }

        Map<Integer, Double> paymentStatusAggr = customOrderAggregatedRepository
                .getProfitDataForChart(startDate, endDate, OrderStatusEnum.PAYMENT, aggregationParameter);
        Map<Integer, Double> cancelledStatusAggr = customOrderAggregatedRepository
                .getProfitDataForChart(startDate, endDate, OrderStatusEnum.CANCELLED, aggregationParameter);

        List<Double> paymentValues = new ArrayList<>(paymentStatusAggr.values());
        List<Double> cancelledValues = new ArrayList<>(cancelledStatusAggr.values());
        List<Double> allValues = calculateAllStatusesForProfit(paymentValues, cancelledValues);

        List<String> axisXLabels = getAxisLabelsByAggregation(paymentStatusAggr.keySet(), aggregationParameter);

        lines.add(new ChartDataDTO<>(paymentValues, "Payment"));
        lines.add(new ChartDataDTO<>(cancelledValues, "Cancelled"));
        lines.add(new ChartDataDTO<>(allValues, "All"));

        stats.setAxisXLabels(axisXLabels);

        stats.setLines(lines);

        return stats;
    }

    public OrdersSummaryDTO getOrdersSummaryInfo() {
        OrdersSummary summaryInfo = customOrderAggregatedRepository.getOrdersSummaryInfo();

        return modelMapper.map(summaryInfo, OrdersSummaryDTO.class);
    }

    private int calculatePassedMinutesSinceDayStart(LocalDateTime time) {
        return time.getHour() * SECONDS_IN_MINUTE + time.getMinute();
    }

    private int calculateTrend(int firstCount, int secondCount) {
        return firstCount == 0 ? 0 : (int) Math.round(
                                            (double) (secondCount - firstCount) / firstCount * AMOUNT_OF_PROCENTS);
    }

    public OrdersProfitDTO getProfitStatistic() {
        OrdersProfit profitInfo = customOrderAggregatedRepository.getProfitInfo();
        final int randomHours = 12;
        final int randomMinutes = 37;
        int yesterdayCommentsCount = calculatePassedMinutesSinceDayStart(LocalDateTime.now()
                                                                .plusHours(randomHours).plusMinutes(randomMinutes));
        int currentCommentsCount = calculatePassedMinutesSinceDayStart(LocalDateTime.now());
        StatisticUnit<Integer> weekCommentsProfit = new StatisticUnit<>(currentCommentsCount,
                                                    calculateTrend(yesterdayCommentsCount, currentCommentsCount));

        int value = profitInfo.getCurrentWeekCount();
        StatisticUnit<Integer> weekOrdersProfit = new StatisticUnit<>(value,
                calculateTrend(profitInfo.getLastWeekCount(), value));

        int profitValue = (int) profitInfo.getCurrentWeekProfit();
        StatisticUnit<Integer> todayProfit = new StatisticUnit<>(profitValue,
                calculateTrend((int) profitInfo.getLastWeekProfit(), profitValue));

        return new OrdersProfitDTO(todayProfit, weekOrdersProfit, weekCommentsProfit);
    }

    public List<OrderTypeStatisticDTO> getStatisticByCountry(String code) {
        List<OrderTypeStatistic> statistics = customOrderAggregatedRepository.getDataGroupedByCountry(code);
        return statistics.stream()
                .map(result -> modelMapper.map(result, OrderTypeStatisticDTO.class)).collect(Collectors.toList());
    }

    private void fillListWithEmptyDates(List<AggregatedData<LocalDateTime>> aggregatedData,
                                        LocalDateTime periodStart, LocalDateTime periodEnd) {
        List<LocalDateTime> days = new ArrayList<>();
        for (LocalDateTime start = periodStart; start.isBefore(periodEnd); start = start.plusDays(1)) {
            days.add(start);
        }

        for (int i = 0; i < days.size(); i++) {
            if (aggregatedData.size() != 0) {
                LocalDateTime actualDate = days.get(i);
                LocalDateTime aggregatedDate = aggregatedData.get(i).getGroup();
                if (actualDate.getDayOfYear() != aggregatedDate.getDayOfYear()) {
                    aggregatedData.add(i, new AggregatedData<>(actualDate, 0, BigDecimal.ZERO));
                }
            }
        }
    }

    public ProfitForTwoMonthChartDTO getProfitChartForTwoMonth() {
        LocalDateTime todayMinusTwoMonths = LocalDateTime.now().minusMonths(2);
        LocalDateTime periodStart = LocalDateTime
                .of(todayMinusTwoMonths.getYear(), todayMinusTwoMonths.getMonth(), 1, 0, 1);
        LocalDateTime periodEnd = periodStart.plusMonths(2);

        List<AggregatedData<LocalDateTime>> aggregatedData = customOrderAggregatedRepository
                .getProfitForDateRangeChartData(periodStart, periodEnd);

        fillListWithEmptyDates(aggregatedData, periodStart, periodEnd);

        final int amountOfPoints = 10;
        int agrValue = 0;
        int size = aggregatedData.size();
        int groupBy = size / amountOfPoints;
        int remain = size % amountOfPoints;
        int sizeWithoutRemain = aggregatedData.size() - remain;
        List<Integer> groupedData = new ArrayList<>();
        for (int i = 0; i < sizeWithoutRemain; i++) {
            agrValue += aggregatedData.get(i).getSum().intValue();
            if ((i + 1) % groupBy == 0) {
                groupedData.add(agrValue);
                agrValue = 0;
            }
        }

        if (remain > 0) {
            for (int i = sizeWithoutRemain; i < aggregatedData.size(); i++) {
                agrValue += aggregatedData.get(i).getSum().intValue();
            }
            groupedData.set(amountOfPoints - 1, groupedData.get(amountOfPoints - 1) + agrValue);
        }

        List<ChartDataDTO<Integer>> chartDataDTOs = new ArrayList<>();
        chartDataDTOs.add(new ChartDataDTO<>(groupedData, null));
        ProfitForTwoMonthChartDTO profitForTwoMonths =
                new ProfitForTwoMonthChartDTO(chartDataDTOs, null, "$", null);

        int sumForFirstMonth = 0;
        int sumForSecondMonth = 0;
        for (AggregatedData<LocalDateTime> data : aggregatedData) {
            int profit = data.getSum().intValue();
            LocalDateTime date = data.getGroup();
            if (date.isAfter(periodStart) && date.isBefore(periodStart.plusMonths(1))) {
                sumForFirstMonth += profit;
            } else {
                sumForSecondMonth += profit;
            }
        }

        String firstMonthTitle = formatStatisticTitle(periodStart, periodEnd.minusMonths(1).minusDays(1));
        String secondMonthTitle = formatStatisticTitle(periodEnd.minusMonths(1), periodEnd.minusDays(1));

        List<ChartAdditionalInfoDTO<Integer>> additionalInfo = new ArrayList<>();
        additionalInfo.add(new ChartAdditionalInfoDTO<>(sumForFirstMonth, firstMonthTitle));
        additionalInfo.add(new ChartAdditionalInfoDTO<>(sumForSecondMonth, secondMonthTitle));
        profitForTwoMonths.setAggregatedData(additionalInfo);
        return profitForTwoMonths;
    }

    private String formatStatisticTitle(LocalDateTime startPeriod, LocalDateTime endPeriod) {
        return startPeriod.getDayOfMonth() + " " + MonthsEnum.from(startPeriod.getMonthValue()) + " - " +
                endPeriod.getDayOfMonth() + " " + MonthsEnum.from(endPeriod.getMonthValue());

    }

    public BaseChartDTO<Integer> getProfitChartForYear() {
        LocalDateTime startPeriod = LocalDateTime.now().minusYears(1);
        LocalDateTime endPeriod = LocalDateTime.now();
        List<OrderStatusEnum> statuses = Arrays.asList(OrderStatusEnum.PAYMENT, OrderStatusEnum.ALL);
        Map<Integer, List<AggregatedData>> resultMap = customOrderAggregatedRepository
                .getProfitChartDataForPeriod(statuses, startPeriod, endPeriod);

        BaseChartDTO<Integer> profitForYear = new BaseChartDTO<>(null, null, "$");

        ChartDataDTO<Integer> chart1 = new ChartDataDTO<>(resultMap.get(0).stream()
                .map(aggregatedData -> aggregatedData.getSum().intValue())
                .collect(Collectors.toList()), "transactions");
        ChartDataDTO<Integer> chart2 = new ChartDataDTO<>(resultMap.get(1).stream()
                .map(aggregatedData -> aggregatedData.getSum().intValue())
                .collect(Collectors.toList()), "orders");

        profitForYear.setLines(Arrays.asList(chart1, chart2));

        return profitForYear;
    }
}

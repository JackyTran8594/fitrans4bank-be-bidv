package com.eztech.fitrans.ecommerce.repository;

import com.eztech.fitrans.ecommerce.entity.AggregatedData;
import com.eztech.fitrans.ecommerce.entity.Country;
import com.eztech.fitrans.ecommerce.entity.Order;
import com.eztech.fitrans.ecommerce.entity.enums.AggregationEnum;
import com.eztech.fitrans.ecommerce.entity.enums.OrderStatusEnum;
import com.eztech.fitrans.ecommerce.entity.enums.OrderTypeEnum;
import com.eztech.fitrans.ecommerce.entity.statistic.OrderTypeStatistic;
import com.eztech.fitrans.ecommerce.entity.statistic.OrdersProfit;
import com.eztech.fitrans.ecommerce.entity.statistic.OrdersSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Join;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.eztech.fitrans.ecommerce.Constants.DAYS_IN_WEEK;
import static com.eztech.fitrans.ecommerce.Constants.MONTHS_IN_YEAR;
import static com.eztech.fitrans.ecommerce.Constants.LAST_HOUR;
import static com.eztech.fitrans.ecommerce.Constants.LAST_MINUTE;

@Repository
@SuppressWarnings("checkstyle:magicnumber")
public class CustomOrderAggregatedRepository {
    private EntityManager entityManager;
    @Autowired
    public CustomOrderAggregatedRepository(EntityManagerFactory entityManagerFactory) {
        entityManager = entityManagerFactory.createEntityManager();
    }

    public Map<Integer, Long> getCountDataForPeriod(LocalDateTime startDate, LocalDateTime endDate,
                                                    OrderStatusEnum status, AggregationEnum aggregation) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery = builder.createQuery(Tuple.class);
        Root<Order> order = criteriaQuery.from(Order.class);

        Map<Integer, Long> emptyMap;
        Expression groupFunction;
        switch (aggregation) {
            case WEEK:
                groupFunction = builder.function("dayOfWeek", Integer.class, order.get("date"));
                emptyMap = AggregationUtils.generateDefaultMap(DAYS_IN_WEEK, 0L);
                break;
            case YEAR:
                groupFunction = builder.function("year", Integer.class, order.get("date"));
                emptyMap = AggregationUtils.generateDefaultMap(startDate.getYear(), endDate.getYear(), 0L);
                break;
            case MONTH:
                groupFunction = builder.function("month", Integer.class, order.get("date"));
                emptyMap = AggregationUtils.generateDefaultMap(MONTHS_IN_YEAR, 0L);
                break;
            default:
                throw new IllegalArgumentException("Wrong aggregation");
        }

        if (status == OrderStatusEnum.ALL) {
            criteriaQuery.where(builder.between(order.get("date"), startDate, endDate));
        } else {
            criteriaQuery.where(builder.and(builder.between(order.get("date"), startDate, endDate)),
                    builder.and(builder.equal(order.get("status"), status))
            );
        }

        criteriaQuery.groupBy(groupFunction);
        criteriaQuery.multiselect(groupFunction, builder.count(order.get("date")));

        List<Tuple> queryResult = entityManager.createQuery(criteriaQuery).getResultList();

        queryResult.forEach(tupleData -> {
            Integer tupleKey = (Integer) tupleData.get(0);
            Long tupleValue = (Long) tupleData.get(1);
            emptyMap.put(tupleKey, tupleValue);
        });

        return emptyMap;
    }

    public Map<Integer, Double> getProfitDataForChart(LocalDateTime startDate, LocalDateTime endDate,
                                                      OrderStatusEnum status, AggregationEnum aggregation) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery = builder.createQuery(Tuple.class);
        Root<Order> order = criteriaQuery.from(Order.class);

        Map<Integer, Double> emptyMap;
        Expression groupFunction;
        switch (aggregation) {
            case WEEK:
                groupFunction = builder.function("dayOfWeek", Integer.class, order.get("date"));
                emptyMap = AggregationUtils.generateDefaultMap(DAYS_IN_WEEK, 0D);
                break;
            case YEAR:
                groupFunction = builder.function("year", Integer.class, order.get("date"));
                emptyMap = AggregationUtils.generateDefaultMap(startDate.getYear(), endDate.getYear(), 0D);
                break;
            case MONTH:
                groupFunction = builder.function("month", Integer.class, order.get("date"));
                emptyMap = AggregationUtils.generateDefaultMap(MONTHS_IN_YEAR, 0D);
                break;
            default:
                throw new IllegalArgumentException("Wrong aggregation");
        }

        if (status == OrderStatusEnum.ALL) {
            criteriaQuery.where(builder.between(order.get("date"), startDate, endDate));
        } else {
            criteriaQuery.where(builder.and(builder.between(order.get("date"), startDate, endDate)),
                    builder.and(builder.equal(order.get("status"), status))
            );
        }

        criteriaQuery.groupBy(groupFunction);
        criteriaQuery.multiselect(groupFunction, builder.sumAsDouble(order.get("value")));

        List<Tuple> queryResult = entityManager.createQuery(criteriaQuery).getResultList();

        queryResult.forEach(tupleData -> {
            Integer tupleKey = (Integer) tupleData.get(0);
            Double tupleValue = ((BigDecimal) tupleData.get(1)).doubleValue();
            emptyMap.put(tupleKey, tupleValue);
        });

        return emptyMap;

    }

    private CriteriaQuery<Long> buildCriteriaQueryForCount(CriteriaBuilder builder,
                                                           LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime today = LocalDateTime.now();
        if (startDate == null) {
            final int defaultYearsBefore = 10;
            startDate = today.minusYears(defaultYearsBefore);
        }
        if (endDate == null) {
            endDate = today;
        }

        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<Order> order = criteriaQuery.from(Order.class);

        criteriaQuery.select(builder.count(order));
        criteriaQuery.where(builder.between(order.get("date"), startDate, endDate));

        return criteriaQuery;

    }

    private CriteriaQuery<Double> buildCriteriaQueryForProfit(CriteriaBuilder builder,
                                                              LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime today = LocalDateTime.now();
        if (startDate == null) {
            final int defaultYearsBefore = 10;
            startDate = today.minusYears(defaultYearsBefore);
        }
        if (endDate == null) {
            endDate = today;
        }

        CriteriaQuery<Double> profitCriteriaQuery = builder.createQuery(Double.class);
        Root<Order> profitOrder = profitCriteriaQuery.from(Order.class);

        profitCriteriaQuery.select(builder.sumAsDouble(profitOrder.get("value")));
        profitCriteriaQuery.where(builder.between(profitOrder.get("date"), startDate, endDate));

        return profitCriteriaQuery;
    }

    public OrdersSummary getOrdersSummaryInfo() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate;
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        int countForAllTime = Math.toIntExact(entityManager
                .createQuery(buildCriteriaQueryForCount(builder, null, null)).getSingleResult());

        startDate = LocalDateTime.of(today.getYear(),
                today.minusMonths(1).getMonth(), 1, 0, 1);
        endDate = LocalDateTime.of(today.getYear(),
                today.getMonth(), 1, LAST_HOUR, LAST_MINUTE).minusDays(1);
        int countForLastMonth = Math.toIntExact(entityManager
                .createQuery(buildCriteriaQueryForCount(builder, startDate, endDate)).getSingleResult());

        LocalDateTime weekBefore = LocalDateTime.now().minusDays(DAYS_IN_WEEK);
        Pair<LocalDateTime, LocalDateTime> weekInterval = AggregationUtils.getWeekInterval(weekBefore);
        startDate = weekInterval.getFirst();
        endDate = weekInterval.getSecond();
        int countForLastWeek = Math.toIntExact(entityManager
                .createQuery(buildCriteriaQueryForCount(builder, startDate, endDate)).getSingleResult());

        LocalDateTime startDay = LocalDateTime.of(today.getYear(), today.getMonth(),
                today.getDayOfMonth(), 0, 1);
        LocalDateTime endDay = LocalDateTime.of(today.getYear(), today.getMonth(),
                today.getDayOfMonth(), LAST_HOUR, LAST_MINUTE);
        int countForLastToday = Math.toIntExact(entityManager
                .createQuery(buildCriteriaQueryForCount(builder, startDay, endDay)).getSingleResult());

        return new OrdersSummary(countForAllTime, countForLastMonth, countForLastWeek, countForLastToday);

    }

    private int getCountForWeek(EntityManager entityManager, CriteriaBuilder builder,
                                LocalDateTime startWeek, LocalDateTime endWeek) {
        return Math.toIntExact(entityManager
                .createQuery(buildCriteriaQueryForCount(builder, startWeek, endWeek))
                .getSingleResult());
    }

    private double getProfitForWeek(EntityManager entityManager, CriteriaBuilder builder,
                                    LocalDateTime startWeek, LocalDateTime endWeek) {
        return (entityManager
                .createQuery(buildCriteriaQueryForProfit(builder, startWeek, endWeek)))
                .getSingleResult();
    }

    public OrdersProfit getProfitInfo() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime weekBefore = LocalDateTime.now().minusDays(DAYS_IN_WEEK);
        Pair<LocalDateTime, LocalDateTime> weekInterval = AggregationUtils.getWeekInterval(weekBefore);

        LocalDateTime lastWeekStartDate = weekInterval.getFirst();
        LocalDateTime lastWeekEndDate = weekInterval.getSecond();
        LocalDateTime currentWeekStartDate = lastWeekEndDate.plusDays(1);

        int countForLastWeek = getCountForWeek(entityManager, builder, lastWeekStartDate, lastWeekEndDate);

        double profitForLastWeek = getProfitForWeek(entityManager, builder, lastWeekStartDate, lastWeekEndDate);

        int countForCurrentWeek = getCountForWeek(entityManager, builder, currentWeekStartDate, today);

        double profitForCurrentWeek = getProfitForWeek(entityManager, builder, currentWeekStartDate, today);

        return new OrdersProfit(countForLastWeek, countForCurrentWeek, profitForLastWeek, profitForCurrentWeek);
    }

    public List<OrderTypeStatistic> getDataGroupedByCountry(String code) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery = builder.createQuery(Tuple.class);
        Root<Order> order = criteriaQuery.from(Order.class);
        Join<Order, Country> postJoin = order.join("country");

        criteriaQuery.where(builder.equal(postJoin.get("code"), code));
        criteriaQuery.groupBy(order.get("type"));
        criteriaQuery.multiselect(order.get("type"), builder.count(order.get("date")));

        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        List<Tuple> queryResult = query.getResultList();

        Map<Integer, Long> resultMap = AggregationUtils.generateDefaultMap(OrderTypeEnum.asList().size(), 0L);

        queryResult.forEach(tupleData -> {
            Integer tupleKey =  ((OrderTypeEnum) tupleData.get(0)).ordinal();
            Long tupleValue = (Long) tupleData.get(1);
            resultMap.put(tupleKey, tupleValue);
        });

        return resultMap.entrySet().stream()
                .map(entry -> new OrderTypeStatistic(entry.getKey(), Math.toIntExact(entry.getValue())))
                .collect(Collectors.toList());

    }

    public List<AggregatedData<LocalDateTime>> getProfitForDateRangeChartData(LocalDateTime startPeriod,
                                                                              LocalDateTime endPeriod) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery = builder.createQuery(Tuple.class);
        Root<Order> order = criteriaQuery.from(Order.class);
        Expression<Integer> groupExpression = builder.function("dayOfYear", Integer.class, order.get("date"));
        criteriaQuery.where(builder.between(order.get("date"), startPeriod, endPeriod));
        criteriaQuery.groupBy(groupExpression);
        criteriaQuery.orderBy(builder.asc(groupExpression));
        criteriaQuery.multiselect(groupExpression, builder.sumAsDouble(order.get("value")));

        List<Tuple> queryResult = entityManager.createQuery(criteriaQuery).getResultList();

        return queryResult.stream().map(tupleData -> {
            Integer tupleKey = (Integer) tupleData.get(0);
            LocalDate date = LocalDate.ofYearDay(startPeriod.getYear(), tupleKey);
            LocalTime time = LocalTime.of(0, 0);
            BigDecimal tupleValue = (BigDecimal) tupleData.get(1);
            LocalDateTime convertedDate = LocalDateTime.of(date, time);
            return new AggregatedData<>(convertedDate, 0, tupleValue);
        }).collect(Collectors.toList());
    }


    public Map<Integer, List<AggregatedData>> getProfitChartDataForPeriod(List<OrderStatusEnum> statuses,
                                                                          LocalDateTime startPeriod,
                                                                          LocalDateTime endPeriod) {
        Map<Integer, List<AggregatedData>> resultMap = new TreeMap<>();
        for (int i = 0; i < statuses.size(); i++) {
            Map<Integer, Double> queryResult = getProfitDataForChart(startPeriod, endPeriod,
                    statuses.get(i), AggregationEnum.MONTH);
            resultMap.put(i, queryResult.values().stream().map(data ->
                    new AggregatedData<>(null, 0, BigDecimal.valueOf(data))
            ).collect(Collectors.toList()));
        }

        return resultMap;
    }
}

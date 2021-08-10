package com.eztech.fitrans.ecommerce.repository;


import com.eztech.fitrans.ecommerce.entity.enums.AggregationEnum;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.eztech.fitrans.ecommerce.Constants.DAYS_IN_WEEK;
import static com.eztech.fitrans.ecommerce.Constants.MONTHS_IN_YEAR;

public class AggregationRepository<T> {

    private EntityManager entityManager;
    private String monthAggregation;
    private BiFunction<CriteriaBuilder, Root<T>, Expression> selectExpression;
    private  Class<T> clazz;


    public AggregationRepository(EntityManager entityManager, String monthAggregation,
                                 BiFunction<CriteriaBuilder, Root<T>, Expression> selectExpression, Class<T> clazz) {
        this.entityManager = entityManager;
        this.monthAggregation = monthAggregation;
        this.selectExpression = selectExpression;
        this.clazz = clazz;
    }

    public Map<Integer, Long> getDataByPeriod(LocalDateTime startDate, LocalDateTime endDate,
                                              AggregationEnum aggregation) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery = builder.createQuery(Tuple.class);
        Root<T> root = criteriaQuery.from(clazz);
        Map<Integer, Long> mapOfAggregatedData;
        javax.persistence.criteria.Expression groupFunction;

        switch (aggregation) {
            case WEEK:
                groupFunction = builder.function("dayOfWeek", Integer.class, root.get("date"));
                mapOfAggregatedData = AggregationUtils.generateDefaultMap(DAYS_IN_WEEK, 0L);
                break;
            case YEAR:
                groupFunction = builder.function("year", Integer.class, root.get("date"));
                mapOfAggregatedData = AggregationUtils.generateDefaultMap(startDate.getYear(),
                        endDate.getYear(), 0L);
                break;
            case MONTH:
                groupFunction = builder.function(monthAggregation, Integer.class, root.get("date"));
                int amountOfEntries;
                if (monthAggregation.equals("month")) {
                    amountOfEntries = MONTHS_IN_YEAR;
                } else {
                    amountOfEntries = endDate.getDayOfYear() - startDate.getDayOfYear();
                }

                mapOfAggregatedData = AggregationUtils.generateDefaultMap(amountOfEntries, 0L);
                break;
            default:
                throw new IllegalArgumentException("Wrong aggregation");
        }
        criteriaQuery.where(builder.between(root.get("date"), startDate, endDate));
        criteriaQuery.groupBy(groupFunction);
        criteriaQuery.multiselect(groupFunction, selectExpression.apply(builder, root));

        List<Tuple> queryResult = entityManager.createQuery(criteriaQuery).getResultList();

        queryResult.forEach(tupleData -> {
            Integer tupleKey = (Integer) tupleData.get(0);
            Long tupleValue = (Long) tupleData.get(1);
            mapOfAggregatedData.put(tupleKey, tupleValue);
        });

        return mapOfAggregatedData;
    }
}

package com.eztech.fitrans.ecommerce.repository;

import com.eztech.fitrans.ecommerce.entity.UserActivity;
import com.eztech.fitrans.ecommerce.entity.enums.AggregationEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class CustomUserActivitiesAggregationRepository {
    private EntityManager entityManager;

    @Autowired
    public CustomUserActivitiesAggregationRepository(EntityManagerFactory entityManagerFactory) {
        entityManager = entityManagerFactory.createEntityManager();
    }

    public Map<Integer, Long> getDataByPeriod(LocalDateTime startDate, LocalDateTime endDate,
                                              AggregationEnum aggregation) {
        AggregationRepository<UserActivity> aggregationRepository = new AggregationRepository<>(
                entityManager, "dayOfMonth", (builder, root) -> builder.count(root.get("date")), UserActivity.class);
        return aggregationRepository.getDataByPeriod(startDate, endDate, aggregation);
    }
}

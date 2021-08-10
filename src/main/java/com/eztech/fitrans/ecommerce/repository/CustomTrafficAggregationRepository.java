package com.eztech.fitrans.ecommerce.repository;

import com.eztech.fitrans.ecommerce.entity.Traffic;
import com.eztech.fitrans.ecommerce.entity.enums.AggregationEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class CustomTrafficAggregationRepository {
    private EntityManager entityManager;

    @Autowired
    public CustomTrafficAggregationRepository(EntityManagerFactory entityManagerFactory) {
        entityManager = entityManagerFactory.createEntityManager();
    }

    public Map<Integer, Long> getDataByPeriod(LocalDateTime startDate, LocalDateTime endDate,
                                              AggregationEnum aggregation) {
        AggregationRepository<Traffic> aggregationRepository = new AggregationRepository<>(
                entityManager, "month", (builder, root) -> builder.sum(root.get("value")), Traffic.class);
        return aggregationRepository.getDataByPeriod(startDate, endDate, aggregation);
    }
}


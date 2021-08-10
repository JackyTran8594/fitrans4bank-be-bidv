package com.eztech.fitrans.ecommerce.entity.builder;

import com.eztech.fitrans.ecommerce.entity.Order;
import com.eztech.fitrans.ecommerce.entity.filter.OrderGridFilter;
import com.eztech.fitrans.ecommerce.entity.specification.OrderSpecification;
import com.eztech.fitrans.ecommerce.entity.specification.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OrderSpecificationBuilder {

    private List<SearchCriteria> params;

    public OrderSpecificationBuilder() {
        params = new ArrayList<>();
    }

    private OrderSpecificationBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    private void initParams(OrderGridFilter filter) {
        if (filter.getFilterByname() != null) {
            with("name", ":", filter.getFilterByname());
        }
        if (filter.getFilterBydate() != null) {
            with("date", ":", filter.getFilterBydate());
        }
        if (filter.getFilterBysum() != null) {
            with("value", ":", filter.getFilterBysum());
        }
        if (filter.getFilterBycountry() != null) {
            with("country", ":", filter.getFilterBycountry());
        }
        if (filter.getFilterBystatus() != null) {
            with("status", ":", filter.getFilterBystatus());
        }
        if (filter.getFilterBytype() != null) {
            with("type", ":", filter.getFilterBytype());
        }
    }

    public Optional<Specification<Order>> build(OrderGridFilter filter) {
        initParams(filter);

        if (params.size() == 0) {
            return Optional.empty();
        }

        Specification<Order> result = new OrderSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new OrderSpecification(params.get(i)));
        }

        return Optional.of(result);
    }
}


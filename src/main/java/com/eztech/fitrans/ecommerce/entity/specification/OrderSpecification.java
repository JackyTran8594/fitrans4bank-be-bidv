package com.eztech.fitrans.ecommerce.entity.specification;

import com.eztech.fitrans.authentication.exception.BadRequestHttpException;
import com.eztech.fitrans.ecommerce.entity.Country;
import com.eztech.fitrans.ecommerce.entity.Order;
import com.eztech.fitrans.ecommerce.entity.enums.OrderStatusEnum;
import com.eztech.fitrans.ecommerce.entity.enums.OrderTypeEnum;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.eztech.fitrans.ecommerce.Constants.DATE;
import static com.eztech.fitrans.ecommerce.Constants.VALUE;
import static com.eztech.fitrans.ecommerce.Constants.COUNTRY;
import static com.eztech.fitrans.ecommerce.Constants.STATUS;
import static com.eztech.fitrans.ecommerce.Constants.NAME;
import static com.eztech.fitrans.ecommerce.Constants.TYPE;
import static com.eztech.fitrans.ecommerce.Constants.DATE_FORMAT;

public class OrderSpecification implements Specification<Order> {

    private static final long serialVersionUID = -4415234963138321694L;

    private transient SearchCriteria criteria;

    public OrderSpecification(SearchCriteria searchCriteria) {
        this.criteria = searchCriteria;
    }

    private List<OrderStatusEnum> generateStatusesByValue(String subString) {
        List<String> statuses = OrderStatusEnum.asList();
        return valueOf(statuses, subString, status -> OrderStatusEnum.valueOf(status.toUpperCase()));
    }

    private List<OrderTypeEnum> generateTypesByValue(String subString) {
        List<String> types = OrderTypeEnum.asList();
        return valueOf(types, subString, type -> OrderTypeEnum.valueOf(type.toUpperCase()));
    }

    private <T> List<T> valueOf(List<String> types, String subString, Function<String, T> enumValueFunc) {
        return types.stream()
                .filter(status -> status.toUpperCase().contains(subString.toUpperCase()))
                .map(enumValueFunc)
                .collect(Collectors.toList());
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria.getOperation().equalsIgnoreCase(":")) {
            switch (criteria.getKey()) {
                case COUNTRY:
                    Join<Order, Country> orderCountry = root.join(COUNTRY);
                    String lowCase = ((String) criteria.getValue()).toLowerCase();
                    return builder.like(builder.lower(orderCountry.get(NAME)), "%" + lowCase + "%");
                case STATUS:
                    CriteriaBuilder.In<OrderStatusEnum> inClauseStatus = builder.in(root.get(STATUS));
                    List<OrderStatusEnum> statuses = generateStatusesByValue(criteria.getValue().toString());
                    for (OrderStatusEnum status : statuses) {
                        inClauseStatus.value(status);
                    }
                    return inClauseStatus;
                case TYPE:
                    CriteriaBuilder.In<OrderTypeEnum> inClauseType = builder.in(root.get(TYPE));
                    List<OrderTypeEnum> types = generateTypesByValue(criteria.getValue().toString());
                    for (OrderTypeEnum type : types) {
                        inClauseType.value(type);
                    }
                    return inClauseType;
                case VALUE:
                    try {
                        String value = (String) criteria.getValue();
                        return builder.equal(root.get(VALUE), new BigDecimal(value));
                    } catch (NumberFormatException ex) {
                        throw new BadRequestHttpException();
                    }
                case DATE:
                    Expression<String> dateStringExpr = builder
                            .function("TO_CHAR", String.class, root.get(DATE), builder.literal(DATE_FORMAT));
                    return builder.like(builder.lower(dateStringExpr), "%" + criteria.getValue() + "%");
                default:
                    return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            }
        }
        return null;
    }
}

package com.eztech.fitrans.ecommerce.repository;

import com.eztech.fitrans.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Modifying
    @Query("delete from Order t where t.id = ?1")
    void delete(Long entityId);
}

package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.TransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long>, TransactionTypeRepositoryCustom {
    @Query(value = "SELECT count(*) FROM profile WHERE type = :type AND status = 'ACTIVE'", nativeQuery = true)
    Long countProfileByTransType(@Param("type") Integer type);
}

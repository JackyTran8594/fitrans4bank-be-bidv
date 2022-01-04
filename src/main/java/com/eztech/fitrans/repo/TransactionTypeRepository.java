package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.TransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long>, TransactionTypeRepositoryCustom {

}

package com.eztech.fitrans.repo;

import java.util.List;

import com.eztech.fitrans.model.Customer;
import com.eztech.fitrans.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryCustom {
     List<Customer> findByCif(String cif);
     
}

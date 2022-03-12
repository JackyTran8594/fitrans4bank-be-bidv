package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryCustom {
     
     // @Query(value = "SELECT * FROM customer WHERE cif LIKE :cif", nativeQuery = true)
     // List<Customer> findByCif(@Param("cif") String cif);
     List<Customer> findByCifContains(String cif);
     

     @Modifying
     @Transactional
     @Query(value = "UPDATE customer SET STATUS = :status WHERE id IN :ids", nativeQuery = true)
     Integer updateStatus(@Param("ids") List<Long> id,
                                       @Param("status") String status);

     @Modifying
     @Transactional
     @Query(value = "DELETE  FROM customer WHERE id IN :ids", nativeQuery = true)
     Integer delete(@Param("ids") List<Long> id);
}

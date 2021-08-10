package com.eztech.fitrans.ecommerce.repository;

import com.eztech.fitrans.ecommerce.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {
    List<Country> findAllByOrderByNameAsc();
}

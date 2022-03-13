package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.StaffContact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffContactRepository extends JpaRepository<StaffContact, Long>, StaffContactRepositoryCustom {

    StaffContact findByCustomerId(Long id);
}

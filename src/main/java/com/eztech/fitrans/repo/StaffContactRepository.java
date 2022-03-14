package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.StaffContact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffContactRepository extends JpaRepository<StaffContact, Long>, StaffContactRepositoryCustom {

    StaffContact findByCustomerId(Long id);

    @Query(value = "SELECT count(*) FROM profile WHERE (staff_id_cm = :id OR staff_id_ct = :id) AND status = 'ACTIVE'", nativeQuery = true)
    Long countProfile(@Param("id") Long id);
}

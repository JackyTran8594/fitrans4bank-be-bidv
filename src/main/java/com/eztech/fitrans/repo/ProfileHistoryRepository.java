package com.eztech.fitrans.repo;


import java.util.List;

import javax.transaction.Transactional;

import com.eztech.fitrans.model.ProfileHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileHistoryRepository extends JpaRepository<ProfileHistory, Long>, ProfileHistoryRepositoryCustom {

    @Transactional
    @Modifying
    @Query(value = "DELETE  FROM profile_history WHERE profile_id = :id", nativeQuery = true)
    Integer deleteByProfileId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM profile_history WHERE profile_id IN :ids", nativeQuery = true)
    Integer deleteListByProfileId(@Param("ids") List<Long> ids);

    // @Query(value = "SELECT * FROM profile_history p WHERE p.profile_id = :id AND p.staff_id = :staffId AND p.state = :state")
    // List<ProfileHistory> findByProfileIdAndStaffIdAndState(@Param("id") Long id, @Param("staffId") Long staffId,@Param("state") Integer state);
    
}

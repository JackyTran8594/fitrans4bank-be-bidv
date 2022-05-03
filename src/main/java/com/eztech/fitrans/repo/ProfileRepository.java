package com.eztech.fitrans.repo;

import java.util.List;

import javax.transaction.Transactional;

import com.eztech.fitrans.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long>, ProfileRepositoryCustom {

    @Modifying
    @Transactional
    @Query(value = "DELETE  FROM profile WHERE id IN :ids", nativeQuery = true)
    Integer deleteList(@Param("ids") List<Long> id);

    @Modifying
    @Query(value = "SELECT p.* from profile as p LEFT JOIN transaction_type as trans ON trans.id = p.type WHERE trans.type in (1,2) AND p.state = :state AND p.staff_id_cm = :staffId ORDER BY p.real_time_received_cm ASC", nativeQuery = true)
    List<Profile> findBySateAndStaffId(@Param("state") Integer state,
            @Param("staffId") Long staffId);

    @Modifying
    @Query(value = "SELECT p.* from profile as p LEFT JOIN transaction_type as trans ON trans.id = p.type WHERE trans.type in (1,2) AND p.id <> :profileId AND p.state = :state AND p.staff_id_cm = :staffId ORDER BY p.real_time_received_cm ASC", nativeQuery = true)
    List<Profile> findBySateAndStaffIdAndIgnore(@Param("state") Integer state,
            @Param("staffId") Long staffId, @Param("profileId") Long profileId);

}

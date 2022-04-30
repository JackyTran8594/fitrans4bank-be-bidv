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
    @Query(value = "SELECT p.* from profile as p LEFT JOIN transaction_type as trans ON trans.id = p.type WHERE trans.type = :type AND p.state = :state AND p.staff_id = :staffId ORDER BY p.process_date ASC", nativeQuery = true)
    List<Profile> findBySateAndTypeAndStaffId(@Param("type") Integer type, @Param("state") Integer state,
            @Param("staffId") Long staffId);

}

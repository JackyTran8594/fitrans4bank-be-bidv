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
        @Query(value = "DELETE  FROM profile WHERE id IN :ids AND state IN (0)", nativeQuery = true)
        Integer deleteList(@Param("ids") List<Long> id);

        @Modifying
        @Query(value = "SELECT p.* from profile as p LEFT JOIN transaction_type as trans ON trans.id = p.type WHERE trans.type in (1,2) AND p.state = :state AND p.staff_id_cm = :staffId AND p.staff_id_ct IS NULL ORDER BY p.time_received_cm ASC", nativeQuery = true)
        List<Profile> findBySateAndStaffId(@Param("state") Integer state,
                        @Param("staffId") Long staffId);

        // dùng cho ưu tiên hồ sơ
        // chỉ cho phép ưu tiên đối với hồ sơ nhận trong ngày (trước 16h)
        @Modifying
        @Query(value = "SELECT p.* from profile as p LEFT JOIN transaction_type as trans ON trans.id = p.type WHERE trans.type in (1,2) AND p.id <> :profileId AND p.state = :state AND p.staff_id_cm = :staffId AND p.staff_id_ct IS NULL AND CAST(p.real_time_received_cm AS DATE) = CAST(CURRENT_TIMESTAMP AS DATE) ORDER BY p.time_received_cm ASC", nativeQuery = true)
        List<Profile> findBySateAndStaffIdAndIgnore(@Param("state") Integer state,
                        @Param("staffId") Long staffId, @Param("profileId") Long profileId);

        @Query(value = "SELECT COUNT(*) FROM profile WHERE state IN :listState", nativeQuery = true)
        long count(@Param("listState") List<Integer> listState);

        @Query(value = "SELECT COUNT(*) FROM profile AS p LEFT JOIN transaction_type AS trans ON trans.id = p.type" +
                " WHERE 1=1 AND state = :state AND trans.type IN :transactionType ", nativeQuery = true)
        Integer countByStateAndType(@Param("state") Integer state, @Param("transactionType") List<Integer> transactionType);

        /**
         * hàm dùng cho đếm số lượng hò sơ theo state đối với QTTD
         * @param state
         * @param username
         * @param transactionType
         * @return
         */
        @Query(value = "SELECT COUNT(*) FROM profile AS p " +
                "LEFT JOIN transaction_type AS trans ON trans.id = p.type \n" +
                "LEFT JOIN user_entity AS ue ON ue.id = p.staff_id_cm \n" +
                "WHERE 1=1 AND state = :state AND trans.type IN :transactionType AND ue.username = :username ", nativeQuery = true)
        Integer countInDayByStateAndUsername(@Param("state") Integer state, @Param("username") String username, @Param("transactionType") List<Integer> transactionType);

       
       

}

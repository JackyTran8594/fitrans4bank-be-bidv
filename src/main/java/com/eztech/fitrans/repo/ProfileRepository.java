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

        // /**
        //  * trả về tổng số hồ sơ trong ngày - bàn giao trước 16h
        //  * 
        //  * @param timeConfig
        //  * @return
        //  */
        // String select = "SELECT COUNT(*) \n";
        // String from = "FROM profile \n";
        // String where1 = "WHERE ((DATEPART(HOUR, real_time_received_cm) < :timeConfig AND CAST(real_time_received_cm AS DATE) = CAST(GETDATE() AS DATE))";
        // String where2 = " OR (DATEPART(HOUR, real_time_received_ct) < :timeConfig AND CAST(real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)))";
        // String where3 = " OR ((DATEPART(HOUR, real_time_received_cm) > :timeConfig AND CAST(real_time_received_cm AS DATE) = CAST(GETDATE() - 1 AS DATE))";
        // String where4 = " OR (DATEPART(HOUR, real_time_received_cm) > :timeConfig AND CAST(real_time_received_ct AS DATE) = CAST(GETDATE() - 1 AS DATE)))";

        // @Query(value = select + from + where1 + where2 + where3 + where4, nativeQuery = true)
        // Integer countProfileInday(@Param("timeConfig") Double timeConfig);

        // /**
        //  * Số bộ đã xử lý trong ngày
        //  * Số bộ đã bàn giao đang chờ phòng xử lý trong ngày
        //  * Số bộ đã trả lại chờ hoàn thiện HS trong ngày
        //  */
        // String where5 = " AND state = :state";
        // @Query(value = select + from + where1 + where2 + where3 + where4 + where5, nativeQuery = true)
        // Integer countProfileInDayByState(@Param("timeConfig") Double timeConfig, @Param("state") Integer state);

        // /**
        //  * Số bộ dự kiến sẽ xử lý
        //  * Đếm hồ sơ theo luồng dựa vào listState:
        //  * Luồng 1: QLKH - QTTD - GDKH: 0,1,2,3,8,9
        //  * Luồng 2: QLKH - QTTD: 0, 1, 3, 8, 9
        //  * Luồng 3: QLKH - GDKH: 0, 1, 3, 8, 9
        //  * 
        //  * @param listState
        //  * @return
        //  */
        // String from2 = "FROM profile AS p \n";
        // String join1 = "LEFT JOIN transaction_type AS trans ON p.type = trans.id \n";
        // String where6 = " AND state IN :listState AND trans.type = :transactionType";
        // @Query(value = select + from2 + join1 + where1 + where2 + where3 + where4 + where6 , nativeQuery = true)
        // Integer countProfileExpectetWithListState(@Param("timeConfig") Double timeConfig, @Param("listState") List<Integer> listState,
        //                 @Param("transactionType") Integer transactionType);

}

package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.ActionLog;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long>, ActionLogRepositoryCustom {

    /**
     * đếm số lượng action POST - hành động ưu tiên hồ sơ - thành công với priorityProfile trong ngày
     */
    String select = "SELECT count(*) \n";
    String from = "FROM action_log as al \n";
    String join1 = "JOIN user_entity as ue ON al.username = ue.username \n";
    String join2 = "JOIN department as d ON d.id = ue.department_id \n";
    String where = "WHERE al.url LIKE CONCAT('%',:api ,'%') AND department_id = :deparmentId AND CAST(process_time AS DATE) = CAST(GETDATE() AS DATE) and al.http_status = 200 and al.method = 'POST'";
    @Query(value = select + from + join1 + join2 + where, nativeQuery = true)
    Integer countActionLogPriorityProfile(@Param("api") String api, @Param("deparmentId") Long deparmentId);
}

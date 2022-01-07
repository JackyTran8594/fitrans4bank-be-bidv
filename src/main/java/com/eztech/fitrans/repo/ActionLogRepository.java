package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long>, ActionLogRepositoryCustom {

}

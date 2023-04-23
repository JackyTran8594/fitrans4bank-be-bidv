package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.ProfileListDashBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Component("DashboardRepository")
public interface DashboardRepository extends ReadOnlyRepository<ProfileListDashBoard, Long>, DashboardRepositoryCustom {
    List<ProfileListDashBoard> profileInDayByListStateCM(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> parameters);

    List<ProfileListDashBoard> profileInDayByListStateCT(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> parameters);
}

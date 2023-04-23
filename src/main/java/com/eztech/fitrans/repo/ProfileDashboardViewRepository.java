package com.eztech.fitrans.repo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.eztech.fitrans.model.view.ProfileDashBoardView;

@Component("ProfileDashboardViewRepository")
public interface ProfileDashboardViewRepository extends ReadOnlyRepository<ProfileDashBoardView, Long> {
    
    List<ProfileDashBoardView> profileInDayByListStateCM(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> params);

    List<ProfileDashBoardView> profileInDayByListStateCT(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> params);

    List<ProfileDashBoardView> countProfileInDayByListState(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> parameters);
}

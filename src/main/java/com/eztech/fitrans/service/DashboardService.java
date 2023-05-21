package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.dashboard.ProfileListDashBoardDTO;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    List<ProfileListDashBoardDTO> profileInDayByListStateCM(List<Integer> state, String code, List<Integer> transactionType, Map<String, Object> parameters);

    List<ProfileListDashBoardDTO> profileInDayByListStateCT(List<Integer> state, String code, List<Integer> transactionType, Map<String, Object> parameters);

    List<ProfileListDashBoardDTO> profileInDayByListStateCusMan(List<Integer> state, String code, List<Integer> transactionType,  Integer departmentId ,Map<String, Object> parameters);
}

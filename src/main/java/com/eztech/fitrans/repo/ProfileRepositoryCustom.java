package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.model.Profile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProfileRepositoryCustom extends BaseRepositoryCustom {
    ProfileDTO detailByIdAndState(Long id, Integer state);

    ProfileDTO detailById(Long id);

    List<ProfileDTO> listDashboard();

    List<ProfileDTO> getProfileWithParams(Map<String, Object> params, Boolean isAsc);

    // using to get top profile for repeat row
    List<ProfileDTO> getProfileDashboard(Map<String, Object> paramSearch);

    List<Profile> countProfileInday(Integer time, Integer minutes);

    /**
     * Số bộ đã xử lý trong ngày
     * Số bộ đã bàn giao đang chờ phòng xử lý trong ngày
     * Số bộ đã trả lại chờ hoàn thiện HS trong ngày
     */
    List<Profile> countProfileInDayByState(Integer time, Integer minutes, Integer state);

    /**
     * Số bộ đã xử lý trong ngày
     * Số bộ đã bàn giao đang chờ phòng xử lý trong ngày
     * Số bộ đã trả lại chờ hoàn thiện HS trong ngày
     */
    List<Profile> countProfileInDayByListState(List<Integer> state,
                                               List<Integer> transactionType, String code, Map<String, Object> parameters);

    List<Profile> countProfileByListState(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> parameters);

    List<ProfileDTO> profileInDayByListState(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> parameters);

    List<ProfileDTO> profileByListState(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> parameters);


}

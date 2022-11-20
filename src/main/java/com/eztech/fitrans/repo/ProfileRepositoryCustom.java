package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.model.Profile;

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
     * Số bộ dự kiến sẽ xử lý
     * Đếm hồ sơ theo luồng dựa vào listState:
     * Luồng 1: QLKH - QTTD - GDKH: 0,1,2,3,8,9
     * Luồng 2: QLKH - QTTD: 0, 1, 3, 8, 9
     * Luồng 3: QLKH - GDKH: 0, 1, 3, 8, 9
     * 
     * @param listState
     * @return
     */
    List<Profile> countProfileExpectetWithListState(Integer time, Integer minutes, List<Integer> listState, Integer transactionType);


}

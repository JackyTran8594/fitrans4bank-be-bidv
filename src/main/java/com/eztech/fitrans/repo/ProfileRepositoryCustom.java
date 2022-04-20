package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.ProfileDTO;

import java.util.List;
import java.util.Map;

public interface ProfileRepositoryCustom extends BaseRepositoryCustom {
    ProfileDTO detailByIdAndState(Long id, Integer state);

    ProfileDTO detailById(Long id);

    List<ProfileDTO> listDashboard();

    List<ProfileDTO> getProfileWithParams(Map<String, Object> params);
}

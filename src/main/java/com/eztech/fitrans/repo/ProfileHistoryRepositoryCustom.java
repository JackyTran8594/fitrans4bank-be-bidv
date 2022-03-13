package com.eztech.fitrans.repo;

import java.util.List;

import com.eztech.fitrans.dto.response.ProfileHistoryDTO;

public interface ProfileHistoryRepositoryCustom extends BaseRepositoryCustom {

    List<ProfileHistoryDTO> deteilByIdAndState(Long id, Integer state);

    List<ProfileHistoryDTO> profileHistoryDetail(Long id);

}

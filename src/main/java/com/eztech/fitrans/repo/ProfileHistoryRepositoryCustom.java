package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.ProfileHistoryDTO;

public interface ProfileHistoryRepositoryCustom extends BaseRepositoryCustom {
    ProfileHistoryDTO detailById(Long id);
}

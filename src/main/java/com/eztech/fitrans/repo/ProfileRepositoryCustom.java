package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.ProfileDTO;

public interface ProfileRepositoryCustom extends BaseRepositoryCustom {
    ProfileDTO detailById(Long id);
}

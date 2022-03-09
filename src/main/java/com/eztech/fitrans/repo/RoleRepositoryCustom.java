package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.MenuRoleTreeDTO;
import com.eztech.fitrans.dto.response.RoleTreeDTO;

import java.util.List;

public interface RoleRepositoryCustom extends BaseRepositoryCustom {
    List<RoleTreeDTO>  mapRoleList();

    List<RoleTreeDTO> mapMenuRole();
}

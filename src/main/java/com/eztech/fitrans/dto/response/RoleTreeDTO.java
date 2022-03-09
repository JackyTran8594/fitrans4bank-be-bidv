package com.eztech.fitrans.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleTreeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String menu;
    private String description;
    private String code;

    private List<RoleListDTO> children;

    private List<MenuRoleTreeDTO> subMenu;

  
}
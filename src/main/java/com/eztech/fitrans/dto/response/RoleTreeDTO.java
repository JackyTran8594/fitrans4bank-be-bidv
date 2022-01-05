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
    private List<RoleListDTO> children;
}
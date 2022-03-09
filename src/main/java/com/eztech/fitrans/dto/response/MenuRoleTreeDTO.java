package com.eztech.fitrans.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuRoleTreeDTO implements Serializable {

    public String menu;
    public String menuName;
    public String parentCode;
    public String code;
    public String description;
    public Integer depth;

}

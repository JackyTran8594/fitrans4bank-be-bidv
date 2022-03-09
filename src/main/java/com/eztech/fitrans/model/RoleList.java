package com.eztech.fitrans.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.MenuRoleTreeDTO;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "role_list")
@SqlResultSetMapping(name = Constants.ResultSetMapping.MENU_ROLE_DTO, classes = {
        @ConstructorResult(targetClass = MenuRoleTreeDTO.class, columns = {
                @ColumnResult(name = "menu", type = String.class),
                @ColumnResult(name = "menu_name", type = String.class),
                @ColumnResult(name = "parent_code", type = String.class),
                @ColumnResult(name = "code", type = String.class),
                @ColumnResult(name = "description", type = String.class),
        })
})
public class RoleList implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String description;
    @Column(name = "parent_code")
    private String parentCode;
    private String menu;
    private String menuName;

    private String url;
    private String method;
}
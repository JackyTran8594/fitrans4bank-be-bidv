package com.eztech.fitrans.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "role_map")
public class RoleMap implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "roleId")
    private Long roleId;
    @Column(name = "role_list_id")
    private Long roleListId;
}
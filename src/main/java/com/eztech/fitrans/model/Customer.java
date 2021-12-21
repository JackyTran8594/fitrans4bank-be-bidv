package com.eztech.fitrans.model;

import com.eztech.fitrans.constants.CustomerTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cif;
    private String name;
    private String address;
    private String tel;

    ////Loai khach hang (VIP, THONG THUONG)
    @Basic
    private Integer type;
    @Transient
    private CustomerTypeEnum typeEnum;

    @PostLoad
    void fillTransient() {
        if (type != null) {
            this.typeEnum = CustomerTypeEnum.of(type);
        }
    }

    @PrePersist
    void fillPersistent() {
        if (typeEnum != null) {
            this.type = typeEnum.getValue();
        }
    }
}

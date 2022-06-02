package com.eztech.fitrans.model;

import com.eztech.fitrans.constants.CustomerTypeEnum;
import com.eztech.fitrans.dto.response.CustomerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.eztech.fitrans.constants.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(name = Constants.ResultSetMapping.CUSTOMER_DTO, classes = {
    @ConstructorResult(targetClass = CustomerDTO.class, columns = {
            @ColumnResult(name = "id", type = Long.class),
            @ColumnResult(name = "cif", type = String.class),
            @ColumnResult(name = "name", type = String.class),
            @ColumnResult(name = "address", type = String.class),
            @ColumnResult(name = "tel", type = String.class),
            @ColumnResult(name = "type", type = String.class),
            @ColumnResult(name = "created_by", type = String.class),
            @ColumnResult(name = "created_date", type = LocalDateTime.class),
            @ColumnResult(name = "last_updated_by", type = String.class),
            @ColumnResult(name = "last_updated_date", type = LocalDateTime.class),
            @ColumnResult(name = "status", type = String.class),
            @ColumnResult(name = "staff_id", type = Long.class),
            @ColumnResult(name = "staff_id_cm", type = Long.class),
            @ColumnResult(name = "staffName", type = String.class),
            @ColumnResult(name = "staffNameCM", type = String.class),
    })
})
public class Customer extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cif;
    private String name;
    private String address;
    private String tel;
    private Long staffId;
    private Long staffId_CM;

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

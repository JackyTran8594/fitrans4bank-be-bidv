package com.eztech.fitrans.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.StaffContactDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(name = Constants.ResultSetMapping.STAFF_CONTACT_DTO, classes = {
        @ConstructorResult(targetClass = StaffContactDTO.class, columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "cif", type = String.class),
                @ColumnResult(name = "customer_id", type = String.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "created_by", type = String.class),
                @ColumnResult(name = "created_date", type = LocalDateTime.class),
                @ColumnResult(name = "last_updated_by", type = String.class),
                @ColumnResult(name = "last_updated_date", type = LocalDateTime.class),
                @ColumnResult(name = "note", type = String.class),
                @ColumnResult(name = "staff_id_cm", type = String.class),
                @ColumnResult(name = "staff_id_ct", type = String.class),
                @ColumnResult(name = "staff_id_customer", type = String.class),
                @ColumnResult(name = "staffNameCM", type = String.class),
                @ColumnResult(name = "staffNameCustomer", type = String.class),
                @ColumnResult(name = "staffNameCT", type = String.class),
        })
})
@Table(name = "staff_contact")
public class StaffContact extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cif")
    private String cif;
    @Column(name = "staff_id_cm")
    private String staffIdCM;
    @Column(name = "staff_id_ct")
    private String staffIdCT;
    @Column(name = "staff_id_customer")
    private String staffIdCustomer;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "note")
    private String note;
}

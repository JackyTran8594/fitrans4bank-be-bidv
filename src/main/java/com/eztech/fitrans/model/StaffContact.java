package com.eztech.fitrans.model;

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

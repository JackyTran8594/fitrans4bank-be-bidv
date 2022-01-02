package com.eztech.fitrans.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class StaffContact extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cif;
    private String staffIdCM;
    private String staffIdCT;
    private String staffIdCustomer;
    private String customer_id;
    private String note;
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastUpdatedByl;
    private LocalDateTime lastUpdateDate;
}

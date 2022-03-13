package com.eztech.fitrans.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class StaffContactDTO implements Serializable {
    public Long id;
    public String cif;
    public Long staffIdCM;
    public Long staffIdCT;
    public Long staffIdCustomer;
    public String staffNameCM;
    public String staffNameCT;
    public String staffNameCustomer;
    public Long customerId;
    public String note;
    public String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime createdDate;
    public String lastUpdatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime lastUpdateDate;
    public String status;


    public StaffContactDTO (Long id,
        String cif,
        Long customerId,
        String status,
        String createdBy,
        LocalDateTime createdDate,
        String lastUpdatedBy,
        LocalDateTime lastUpdateDate,
        String note,
        Long staffIdCM,
        Long staffIdCT,
        Long staffIdCustomer,
        String staffNameCM,
        String staffNameCT,
        String staffNameCustomer
    ) {
        this.id = id;
        this.cif = cif;
        this.customerId = customerId;
        this.status = status;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdateDate = lastUpdateDate;
        this.note = note;
        this.staffIdCM = staffIdCM;
        this.staffIdCT = staffIdCT;
        this.staffIdCustomer = staffIdCustomer;
        this.staffNameCM = staffNameCM;
        this.staffNameCT = staffNameCT;
        this.staffNameCustomer = staffNameCustomer;
    };

}

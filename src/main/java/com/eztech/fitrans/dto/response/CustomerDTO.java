package com.eztech.fitrans.dto.response;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.CustomerTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
// @AllArgsConstructor
public class CustomerDTO extends BaseImportDTO implements Serializable {

    public Long id;
    public String cif;
    public String name;
    public String address;
    public String tel;
    public Long staffId;
    public Long staffId_CM;
    public String username;
    public String staffName;
    public String staffNameCM;
    // Loai khach hang (VIP, THONG THUONG)
    public Integer type;
    public String typeName;

    public String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime createdDate;
    public String lastUpdatedBy;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime lastUpdatedDate;
    public String status;

    public CustomerDTO(
            Long id,
            String cif,
            String name,
            String address,
            String tel,
            String type,
            String createdBy,
            LocalDateTime createdDate,
            String lastUpdatedBy,
            LocalDateTime lastUpdatedDate,
            String status,
            Long staffId,
            Long staffId_CM,
            String staffName,
            String staffNameCM) {
        this.id = id;
        this.cif = cif;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
        this.staffId = staffId;
        this.staffId_CM = staffId_CM;
        this.staffName = staffName;
        this.staffNameCM = staffNameCM;
        this.type = Integer.parseInt(type);
        if (type != null) {
            this.typeName = CustomerTypeEnum.of(Integer.parseInt(type)).getName();
        }
    }

    // For import excel
    public Integer stt;
    public String errorMsg;
    public Boolean error = false;

    public void fillTransient() {
        if (type != null) {
            this.typeName = CustomerTypeEnum.of(type).getName();
        }
    }
}

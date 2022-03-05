package com.eztech.fitrans.dto.response;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.ProfilePriorityEnum;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.constants.ProfileTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// @JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileHistoryDTO implements Serializable {

    public Long id;

    public Long profileId;

    public Long staffId; // Cán bộ đang thực hiện

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime timeReceived; // Ngày bàn giao QTTD - CM: Credit Management

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime standardTime; // Ngày bàn giao GDKH - CT: Customer-Transaction

    // Trạng thái hồ sơ
    public Integer state;
    public String stateEnum;

    public String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime createdDate;
    public String lastUpdatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime lastUpdatedDate;
    public String status;
    public String staffName;
    public String departmentCode;

    public ProfileHistoryDTO(Long id, 
    Long profileId,
    Long staffId, 
    LocalDateTime timeReceived,
    LocalDateTime standardTime,
            String createdBy, 
            LocalDateTime createdDate, 
            String lastUpdatedBy, 
            LocalDateTime lastUpdatedDate,
            String status,
            Integer state,
            String staffName,
            String departmentCode
           ) {
        this.id = id;
        this.state = state;
        this.profileId = profileId;
        this.staffId = staffId;
        this.timeReceived = timeReceived;
        this.staffName = staffName;
        if (state != null) {
            this.stateEnum = ProfileStateEnum.of(state).getName();
        }
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
        this.departmentCode = departmentCode;
    }

    public void fillTransient() {
       
        if (state != null) {
            this.stateEnum = ProfileStateEnum.of(state).getName();
        }
    }
}

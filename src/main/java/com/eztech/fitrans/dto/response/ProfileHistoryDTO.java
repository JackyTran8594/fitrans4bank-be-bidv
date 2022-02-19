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

    private Long id;

    private Long profileId;

    private Long staffId; // Cán bộ đang thực hiện

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeReceived; // Ngày bàn giao QTTD - CM: Credit Management

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime standardTime; // Ngày bàn giao GDKH - CT: Customer-Transaction

    // Trạng thái hồ sơ
    private Integer state;
    private String stateEnum;

    private String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdDate;
    private String lastUpdatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdatedDate;
    private String status;


    // p.id,p.customer_id,p.staff_id,p.type,p.priority,p.process_date,
    // p.time_received_ct,
    // p.time_received_cm, p.end_time, p.staff_id_cm, p.staff_id_ct,
    // p.number_of_bill,
    // p.number_of_po, p.value, p.return_reason, p.category_profile, p.created_by,
    // p.created_date,p.last_updated_by,p.last_updated_date,p.status,p.state,
    // p.rate,
    // p.notify_by_email ,c.cif,c.name as customer_name, s.name as staff_name

    public ProfileHistoryDTO(Long id, 
    Long staffId, 
            String createdBy, 
            LocalDateTime createdDate, 
            String lastUpdatedBy, 
            LocalDateTime lastUpdatedDate,
            String status,
            Integer state
           ) {
        this.id = id;
        this.state = state;
        if (state != null) {
            this.stateEnum = ProfileStateEnum.of(state).getName();
        }
        this.staffId = staffId;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
    }

    public void fillTransient() {
       
        if (state != null) {
            this.stateEnum = ProfileStateEnum.of(state).getName();
        }
    }
}

package com.eztech.fitrans.dto.response;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.ProfilePriorityEnum;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.constants.ProfileTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO implements Serializable {

    private Long id;
    private Long customerid;
    private String customerName;
    private String cif;
    private Integer type;    //Loai giao dich
    private String typeEnum;    //Mức độ
    private Integer priority;    //Mức độ
    private ProfilePriorityEnum priorityValue;    //Mức độ

    //Tinh trang ho so
    private Integer state;
    private String stateEnum;

    private Long staffId;   //Cán bộ đang thực hiện
    private String staffName;   //Cán bộ đang thực hiện

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    protected LocalDateTime processDate;    //Ngày phát sinh

    private String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdDate;
    private String lastUpdatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdatedDate;
    private String status;
    private Integer rate;

    public ProfileDTO(Long id, Long customerid, Long staffId, Integer type,
                      Integer priority, LocalDateTime processDate,
                      String createdBy, LocalDateTime createdDate, String lastUpdatedBy, LocalDateTime lastUpdatedDate,
                      String status, Integer state, String cif, String customerName,
                      String staffName, Integer rate) {
        this.id = id;
        this.customerid = customerid;
        this.cif = cif;
        this.customerName = customerName;
        this.type = type;
        if (type != null) {
            this.typeEnum = ProfileTypeEnum.of(type).getName();
        }
        this.priority = priority;
        if (priority != null) {
            this.priorityValue = ProfilePriorityEnum.of(priority);
        }
        this.state = state;
        if (state != null) {
            this.stateEnum = ProfileStateEnum.of(state).getName();
        }
        this.staffId = staffId;
        this.staffName = staffName;
        this.processDate = processDate;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
        this.rate = rate;
    }

    public void fillTransient() {
        if (priority != null) {
            this.priorityValue = ProfilePriorityEnum.of(priority);
        }

        if (type != null) {
            this.typeEnum = ProfileTypeEnum.of(type).getName();
        }

        if (state != null) {
            this.stateEnum = ProfileStateEnum.of(state).getName();
        }
    }
}

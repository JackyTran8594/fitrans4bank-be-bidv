package com.eztech.fitrans.dto.response;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.ProfilePriorityEnum;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.constants.ProfileTypeEnum;
import com.eztech.fitrans.constants.ProfileStateProcess;
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
public class ProfileDTO implements Serializable {

    public Long id;
    public Long customerid;
    public String customerName;
    public String cif;
    public Integer type; // Loai giao dich
    public String typeEnum; // Mức độ
    public Integer priority; // Mức độ
    public ProfilePriorityEnum priorityValue; // Mức độ
    public Integer transactionType;
    // private String companyName;

    // Tinh trang ho so
    public Integer state;
    public String stateEnum;

    public Integer profileProcessState;
    public String processStateEnum;

    public Long staffId; // Cán bộ đang thực hiện
    public String staffName; // Cán bộ đang thực hiện

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime processDate; // Ngày phát sinh

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime timeReceived_CM; // Ngày bàn giao QTTD - CM: Credit Management

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime timeReceived_CT; // Ngày bàn giao GDKH - CT: Customer-Transaction

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime endTime; // Ngày kết thúc giao dịch

    public Integer numberOfBill; // Số lượng hóa đơn

    public Integer numberOfPO; // Số lượng ủy nhiệm chi

    public Long staffId_CM; // Cán bộ phòng QTTD

    public Long staffId_CT; // Cán bộ phòng GDKH
    public String returnReason; // Cán bộ phòng GDKH
    public String categoryProfile;
    public BigDecimal value; // Giá trị

    public String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime createdDate;
    public String lastUpdatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime lastUpdatedDate;
    public String status;
    public Integer review;
    public String reviewNote;
    public String note;
    public Boolean notifyByEmail;

    public Integer standardTimeCM;
    public Integer standardTimeCT;
    public Integer standardTimeChecker;
    public Integer additionalTime;

    public String username;

    public String staffNameCM;
    public String staffNameCT;
    public String staffNameLast;


    // p.id,p.customer_id,p.staff_id,p.type,p.priority,p.process_date, p.time_received_ct,
    // p.time_received_cm, p.end_time, p.staff_id_cm, p.staff_id_ct, p.number_of_bill, 
    // p.number_of_po, p.value, p.return_reason, p.category_profile, p.created_by,
    // p.created_date,p.last_updated_by,p.last_updated_date,p.status,p.state, p.rate, 
    // p.notify_by_email ,c.cif,c.name as customer_name, s.name as staff_name , p.review_note

    public ProfileDTO(Long id, 
    Long customerid, 
    Long staffId, 
    Integer type,
            Integer priority, 
            LocalDateTime processDate,
            String createdBy, 
            LocalDateTime createdDate, 
              String lastUpdatedBy, 
            LocalDateTime lastUpdatedDate,
            String status,
            Integer state,
            Integer review,
            Boolean notifyByEmail,
            Long staffId_CM,
            Long staffId_CT,
            Integer numberOfBill,
            Integer numberOfPO,
            BigDecimal value,
            LocalDateTime timeReceived_CT,
            LocalDateTime timeReceived_CM,
            LocalDateTime endTime,
            String returnReason,
            String categoryProfile,
            String cif, 
            String reviewNote,
            String note,
            Integer profileProcessState,
            Integer additionalTime,
            String staffNameLast,
            String customerName,
            String staffName,
            String staffNameCM,
            String staffNameCT,
            Integer transactionType
           ) {
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

        this.profileProcessState = profileProcessState;
        if(profileProcessState != null) {
            this.processStateEnum = ProfileStateProcess.of(profileProcessState).getName();
        }
        this.staffId = staffId;
        this.staffName = staffName;
        this.processDate = processDate;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
        this.review = review;
        this.notifyByEmail = notifyByEmail;
        this.timeReceived_CM = timeReceived_CM;
        this.timeReceived_CT = timeReceived_CT;
        this.endTime = endTime;
        this.numberOfBill = numberOfBill;
        this.numberOfPO = numberOfPO;
        this.staffId_CM = staffId_CM;
        this.staffId_CT = staffId_CT;
        this.returnReason = returnReason;
        this.value = value;
        this.categoryProfile = categoryProfile;
        this.reviewNote = reviewNote;
        this.note = note;
        this.additionalTime = additionalTime;
        this.staffNameCM = staffNameCM;
        this.staffNameCT = staffNameCT;
        this.transactionType = transactionType;
        this.staffNameLast = staffNameLast;
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

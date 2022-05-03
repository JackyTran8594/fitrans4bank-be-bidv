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

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime realTimeReceivedCT; // Ngày thực tế nhận tại QTTD
 
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime realTimeReceivedCM; // Ngày thực tế nhận tại GDKH

    public Integer numberOfBill; // Số lượng hóa đơn

    public Integer numberOfPO; // Số lượng ủy nhiệm chi

    public Long staffId_CM; // Cán bộ phòng QTTD

    public Long staffId_CT; // Cán bộ phòng GDKH
    public String returnReason; // Cán bộ phòng GDKH
    public String categoryProfile;
    public BigDecimal value; // Giá trị

    public String transactionDetail; // Chi tiết giao dịch
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
    public String othersProfile;
    public String currency;
    public Integer additionalTimeMax;

    public String username;

    public String staffNameCM;
    public String staffNameCT;
    public String staffNameLast;
    public String description;
    public Integer priorityNumber;
    public Integer customerType;

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
            LocalDateTime timeReceived_CM,
            LocalDateTime timeReceived_CT,
            LocalDateTime endTime,
            String returnReason,
            String categoryProfile,
            String cif,
            String reviewNote,
            String note,
            Integer additionalTime,
            String othersProfile,
            String currency,
            String description,
            Integer priorityNumber,
            LocalDateTime realTimeReceivedCT,
            LocalDateTime realTimeReceivedCM,
            String staffNameLast,
            String customerName,
            String staffName,
            String staffNameCM,
            String staffNameCT,
            Integer transactionType,
            String transactionDetail,
            Integer additionalTimeMax,
            Integer customerType) {
        this.id = id;
        this.customerid = customerid;
        this.cif = cif;
        this.customerName = customerName;
        this.type = type;
        if (type != null) {
            // this.typeEnum = ProfileTypeEnum.of(type).getName();
            this.typeEnum = "";
        }
        // this.typeEnum = typeEnum;
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
        this.othersProfile = othersProfile;
        this.currency = currency;
        this.description = description;
        this.staffNameCM = staffNameCM;
        this.staffNameCT = staffNameCT;
        this.transactionType = transactionType;
        this.staffNameLast = staffNameLast;
        this.transactionDetail = transactionDetail;
        this.additionalTimeMax = additionalTimeMax;
        this.priorityNumber = priorityNumber;
        this.customerType = customerType;
        this.realTimeReceivedCT = realTimeReceivedCT;
        this.realTimeReceivedCM = realTimeReceivedCM;
    }

    public void fillTransient() {
        if (priority != null) {
            this.priorityValue = ProfilePriorityEnum.of(priority);
        }
        ;

        // if (type != null) {
        // this.typeEnum = ProfileTypeEnum.of(type).getName();
        // }

        if (state != null) {
            this.stateEnum = ProfileStateEnum.of(state).getName();
        }
        ;
    }
}

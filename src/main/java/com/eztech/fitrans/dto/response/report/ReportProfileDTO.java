package com.eztech.fitrans.dto.response.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.constants.ProfileTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
// @AllArgsConstructor
@NoArgsConstructor
public class ReportProfileDTO {

    public Long id;
    public Long customerId;
    public String customerName;
    public String cif;

    public Integer type; // Loai giao dich
    public String typeEnum; // Mức độ

    public Integer transactionType;
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
    public LocalDateTime processDateCT; // Ngày phát sinh

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
    public LocalDateTime endTimeCM; // Ngày kết thúc giao dịch QTTD

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime endTimeCT; // Ngày kết thúc giao dịch GDKH

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime realTimeReceivedCM; // Ngày thực tế nhận tại QTTD

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime realTimeReceivedCT; // Ngày thực tế nhận tại GDKH

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime timeReceivedHistory; // Ngày nhận tại bảng history, dùng để lưu lịch sử các lần quét

    public Integer numberOfBill; // Số lượng hóa đơn

    public Integer numberOfPO; // Số lượng ủy nhiệm chi

    public Long staffId_CM; // Cán bộ phòng QTTD

    public Long staffId_CT; // Cán bộ phòng GDKH

    public String returnReason; // Cán bộ phòng GDKH

    public String categoryProfile;

    public BigDecimal value; // Giá trị

    public String transactionDetail; // Chi tiết giao dịch

    public Integer review;

    public String reviewNote;

    public String note;

    public String pendingNote;

    public Boolean notifyByEmail;

    public String othersProfile;

    public String currency;

    public Integer additionalTimeMax;

    public String staffNameCM;

    public String staffNameCT;

    public String staffNameLast;

    public String description;

    public Integer customerType;

    public Integer no;

    public String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime createdDate;
    public String lastUpdatedBy;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime lastUpdatedDate;
    public String status;

    public ReportProfileDTO(
            Long id,
            Long customerId,
            String customerName,
            String cif,
            Integer type,
            String typeEnum,
            Integer transactionType,
            Integer state,
            String stateEnum,
            Long staffId,
            String staffName,
            LocalDateTime processDate,
            LocalDateTime timeReceived_CM,
            LocalDateTime timeReceived_CT,
            LocalDateTime endTime,
            LocalDateTime realTimeReceivedCM,
            LocalDateTime realTimeReceivedCT,
            LocalDateTime timeReceivedHistory,
            Integer numberOfBill,
            Integer numberOfPO,
            Long staffId_CM,
            Long staffId_CT,
            String returnReason,
            String categoryProfile,
            BigDecimal value,
            String transactionDetail,
            Integer review,
            String reviewNote,
            String note,
            String pendingNote,
            Boolean notifyByEmail,
            String othersProfile,
            String currency,
            Integer additionalTimeMax,
            String staffNameCM,
            String staffNameCT,
            String staffNameLast,
            String description,
            Integer customerType,
            Integer no,
            String createdBy,
            LocalDateTime createdDate,
            String lastUpdatedBy,
            LocalDateTime lastUpdatedDate,
            LocalDateTime endTimeCM,
            LocalDateTime endTimeCT,
            LocalDateTime processDateCT,
            String status) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.cif = cif;
        this.type = type;
        if (this.type != null) {
            this.typeEnum = ProfileTypeEnum.of(this.type).getName();
        }
        this.transactionType = transactionType;
        this.state = state;
        if (this.state != null) {
            this.stateEnum = ProfileStateEnum.of(this.state).getName();
        }
        this.staffId = staffId;
        this.staffName = staffName;
        this.processDate = processDate;
        this.timeReceived_CM = timeReceived_CM;
        this.timeReceived_CT = timeReceived_CT;
        this.endTime = endTime;
        this.realTimeReceivedCM = realTimeReceivedCM;
        this.realTimeReceivedCT = realTimeReceivedCT;
        this.timeReceivedHistory = timeReceivedHistory;
        this.numberOfBill = numberOfBill;
        this.numberOfPO = numberOfPO;
        this.staffId_CM = staffId_CM;
        this.staffId_CT = staffId_CT;
        this.returnReason = returnReason;
        this.categoryProfile = categoryProfile;
        this.value = value;
        this.transactionDetail = transactionDetail;
        this.review = review;
        this.reviewNote = reviewNote;
        this.note = note;
        this.pendingNote = pendingNote;
        this.notifyByEmail = notifyByEmail;
        this.othersProfile = othersProfile;
        this.currency = currency;
        this.additionalTimeMax = additionalTimeMax;
        this.staffNameCM = staffNameCM;
        this.staffNameCT = staffNameCT;
        this.staffNameLast = staffNameLast;
        this.description = description;
        this.customerType = customerType;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
        this.no = no;
        this.processDateCT = processDateCT;
        this.endTimeCM = endTimeCM;
        this.endTimeCT = endTimeCT;
    }

}

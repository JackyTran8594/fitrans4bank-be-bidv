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

    private Long id;
    private Long customerid;
    private String customerName;
    private String cif;
    private Integer type; // Loai giao dich
    private String typeEnum; // Mức độ
    private Integer priority; // Mức độ
    private ProfilePriorityEnum priorityValue; // Mức độ

    // Tinh trang ho so
    private Integer state;
    private String stateEnum;

    private String staffId; // Cán bộ đang thực hiện
    private String staffName; // Cán bộ đang thực hiện

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime processDate; // Ngày phát sinh

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeReceived_CM; // Ngày bàn giao QTTD - CM: Credit Management

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeReceived_CT; // Ngày bàn giao GDKH - CT: Customer-Transaction

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime; // Ngày kết thúc giao dịch

    private Integer numberOfBill; // Số lượng hóa đơn

    private Integer numberOfPO; // Số lượng ủy nhiệm chi

    private String staffId_CM; // Cán bộ phòng QTTD

    private String staffId_CT; // Cán bộ phòng GDKH
    private String returnReason; // Cán bộ phòng GDKH
    private String categoryProfile;
    private BigDecimal value; // Giá trị

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
    private Boolean notifyByEmail;


    // p.id,p.customer_id,p.staff_id,p.type,p.priority,p.process_date, p.time_received_ct,
    // p.time_received_cm, p.end_time, p.staff_id_cm, p.staff_id_ct, p.number_of_bill, 
    // p.number_of_po, p.value, p.return_reason, p.category_profile, p.created_by,
    // p.created_date,p.last_updated_by,p.last_updated_date,p.status,p.state, p.rate, 
    // p.notify_by_email ,c.cif,c.name as customer_name, s.name as staff_name 

    public ProfileDTO(Long id, 
    Long customerid, 
    String staffId, 
    Integer type,
            Integer priority, 
            LocalDateTime processDate,
            LocalDateTime timeReceived_CT,
            LocalDateTime timeReceived_CM,
            LocalDateTime endTime,
            String staffId_CM,
            String staffId_CT,
            Integer numberOfBill,
            Integer numberOfPO,
            BigDecimal value,
            String returnReason,
            String categoryProfile,
            String createdBy, 
            LocalDateTime createdDate, 
            String lastUpdatedBy, 
            LocalDateTime lastUpdatedDate,
            String status,
            Integer state,
            Integer rate,
            Boolean notifyByEmail,
            String cif, 
            String customerName,
            String staffName
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
        this.staffId = staffId;
        this.staffName = staffName;
        this.processDate = processDate;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
        this.rate = rate;
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

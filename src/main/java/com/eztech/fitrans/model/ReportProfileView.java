package com.eztech.fitrans.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@Entity
@Immutable
@Setter
@Getter
public class ReportProfileView extends Auditable<String> implements Serializable {

  @Id
  private Long id;
  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "customer_name")
  private String customerName;

  @Column(name = "cif")
  private String cif;

  @Column(name = "type")
  private Integer type; // Loai giao dich
  // public String typeEnum; // Mức độ

  @Column(name = "transaction_type")
  private Integer transactionType;
  // Tinh trang ho so
  @Column(name = "state")
  private Integer state;
  // public String stateEnum;

  @Column(name = "staff_id")
  private Long staffId; // Cán bộ đang thực hiện
  @Column(name = "staff_name")
  private String staffName; // Cán bộ đang thực hiện

  @Column(name = "process_date")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime processDate; // Ngày phát sinh

  @Column(name = "time_received_cm")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime timeReceived_CM; // Ngày bàn giao QTTD - CM: Credit Management

  @Column(name = "time_received_ct")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime timeReceived_CT; // Ngày bàn giao GDKH - CT: Customer-Transaction

  @Column(name = "endTime")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime endTime; // Ngày kết thúc giao dịch

  @Column(name = "real_time_received_cm")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime realTimeReceivedCM; // Ngày thực tế nhận tại QTTD

  @Column(name = "real_time_received_ct")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime realTimeReceivedCT; // Ngày thực tế nhận tại GDKH

  @Column(name = "time_received_history")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime timeReceivedHistory; // Ngày nhận tại bảng history, dùng để lưu lịch sử các lần quét

  @Column(name = "number_of_bill")
  private Integer numberOfBill; // Số lượng hóa đơn

  @Column(name = "number_of_po")
  private Integer numberOfPO; // Số lượng ủy nhiệm chi

  @Column(name = "staff_id_cm")
  private Long staffId_CM; // Cán bộ phòng QTTD

  @Column(name = "staff_id_ct")
  private Long staffId_CT; // Cán bộ phòng GDKH

  @Column(name = "return_reason")
  private String returnReason; // Cán bộ phòng GDKH

  @Column(name = "category_profile")
  private String categoryProfile;

  @Column(name = "value")
  private BigDecimal value; // Giá trị

  @Column(name = "transaction_detail")
  private String transactionDetail; // Chi tiết giao dịch
 
  @Column(name = "review")
  private Integer review;

  @Column(name = "review_note")
  private String reviewNote;

  @Column(name = "note")
  private String note;

  @Column(name = "pending_note")
  private String pendingNote;

  @Column(name = "notify_by_email")
  private Boolean notifyByEmail;

  // public Integer standardTimeCM;
  // public Integer standardTimeCT;
  // public Integer standardTimeChecker;

  // @Column(name = "additional_time_max")
  // public Integer additionalTime;

  @Column(name = "others_profile")
  private String othersProfile;

  @Column(name = "currency")
  private String currency;

  @Column(name = "additional_time_max")
  private Integer additionalTimeMax;

  // public String username;
  @Column(name = "staff_name_cm")
  private String staffNameCM;

  @Column(name = "staff_name_ct")
  private String staffNameCT;

  @Column(name = "staff_name_last")
  private String staffNameLast;

  @Column(name = "description")
  private String description;

  @Column(name = "customer_type")
  private Integer customerType;

  @Column(name = "no")
  private Integer no;

}

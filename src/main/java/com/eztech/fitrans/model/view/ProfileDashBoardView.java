package com.eztech.fitrans.model.view;

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
import com.eztech.fitrans.model.Auditable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Immutable
public class ProfileDashBoardView extends Auditable<String> implements Serializable {
    
  @Id
  private Long id;

  @Column(name = "customer_id")
  private Long customerid;

  @Column(name = "staff_id")
  private Long staffId; // Cán bộ đang thực hiện

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

  @Column(name = "end_time")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime endTime; // Ngày kết thúc giao dịch


  @Column(name = "real_time_received_ct")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime realTimeReceivedCT; // Ngày thực tế nhận tại QTTD


  @Column(name = "real_time_received_cm")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime realTimeReceivedCM; // Ngày thực tế nhận tại GDKH

  @Column(name = "number_of_bill")
  private Integer numberOfBill; // Số lượng hóa đơn

  @Column(name = "number_of_po")
  private Integer numberOfPO; // Số lượng ủy nhiệm chi

  @Column(name = "staff_id_cm")
  private String staffId_CM; // Cán bộ phòng QTTD

  @Column(name = "staff_id_ct")
  private Long staffId_CT; // Cán bộ phòng GDKH

  @Column(name = "return_reason")
  private String returnReason; // Cán bộ phòng GDKH

  @Column(name = "value")
  private BigDecimal value; // Giá trị

  @Column(name = "category_profile")
  private String categoryProfile; // danh mục hồ sơ

  @Column(name = "cif")
  private String cif; // Giá trị

  // Loai giao dich
  @Basic
  private Integer type;

  @Basic
  private Integer state;


  // Đánh giá
  @Column(name = "review")
  private Integer review;

  @Column(name = "review_note")
  private String reviewNote;

  @Column(name = "note")
  private String note;

  @Column(name = "pending_note")
  private String pendingNote;

  // Gửi email
  @Column(name = "notify_by_email", columnDefinition = "BIT")
  private Boolean notifyByEmail;

  @Column(name = "additional_time")
  private Integer additionalTime; // Số lượng ủy nhiệm chi

  @Column(name = "others_profile")
  private String othersProfile; // hồ sơ khác

  @Column(name = "currency")
  private String currency; // tiền tệ

  @Column(name = "description")
  private String description;

  @Column(name = "customer_name")
  private String customerName;

  @Column(name = "staff_name")
  private String staffName;

  @Column(name = "staff_name_cm")
  private String staffNameCM;

  @Column(name = "staff_name_ct")
  private String staffNameCT;

  @Column(name = "transaction_type")
  private Integer transactionType;

  @Column(name = "transaction_detail")
  private String transactionDetail;


  @Column(name = "time_received_history")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime timeReceivedHistory; // Ngày thực tế nhận tại GDKH

}

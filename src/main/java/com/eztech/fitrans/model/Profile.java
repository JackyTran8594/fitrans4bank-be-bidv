package com.eztech.fitrans.model;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.ProfilePriorityEnum;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.constants.ProfileStateProcess;
import com.eztech.fitrans.constants.ProfileTypeEnum;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(name = Constants.ResultSetMapping.PROFILE_DTO, classes = {
    @ConstructorResult(targetClass = ProfileDTO.class, columns = {
        @ColumnResult(name = "id", type = Long.class),
        @ColumnResult(name = "customer_id", type = Long.class),
        @ColumnResult(name = "staff_id", type = Long.class),
        @ColumnResult(name = "type", type = Integer.class),
        @ColumnResult(name = "priority", type = Integer.class),
        @ColumnResult(name = "process_date", type = LocalDateTime.class),
        @ColumnResult(name = "created_by", type = String.class),
        @ColumnResult(name = "created_date", type = LocalDateTime.class),
        @ColumnResult(name = "last_updated_by", type = String.class),
        @ColumnResult(name = "last_updated_date", type = LocalDateTime.class),
        @ColumnResult(name = "status", type = String.class),
        @ColumnResult(name = "state", type = Integer.class),
        @ColumnResult(name = "review", type = Integer.class),
        @ColumnResult(name = "notify_by_email", type = Boolean.class),
        @ColumnResult(name = "staff_id_cm", type = Long.class),
        @ColumnResult(name = "staff_id_ct", type = Long.class),
        @ColumnResult(name = "number_of_bill", type = Integer.class),
        @ColumnResult(name = "number_of_po", type = Integer.class),
        @ColumnResult(name = "value", type = BigDecimal.class),
        @ColumnResult(name = "time_received_cm", type = LocalDateTime.class),
        @ColumnResult(name = "time_received_ct", type = LocalDateTime.class),
        @ColumnResult(name = "end_time", type = LocalDateTime.class),
        @ColumnResult(name = "return_reason", type = String.class),
        @ColumnResult(name = "category_profile", type = String.class),
        @ColumnResult(name = "cif", type = String.class),
        @ColumnResult(name = "review_note", type = String.class),
        @ColumnResult(name = "note", type = String.class),
        @ColumnResult(name = "profile_process_state", type = Integer.class),
        @ColumnResult(name = "additional_time", type = Integer.class),
        @ColumnResult(name = "others_profile", type = String.class),
        @ColumnResult(name = "currency", type = String.class),
        @ColumnResult(name = "staff_name_last", type = String.class),
        @ColumnResult(name = "customer_name", type = String.class),
        @ColumnResult(name = "staff_name", type = String.class),
        @ColumnResult(name = "staff_name_cm", type = String.class),
        @ColumnResult(name = "staff_name_ct", type = String.class),
        @ColumnResult(name = "transaction_type", type = Integer.class),
        @ColumnResult(name = "transaction_detail", type = String.class),
    })

})
public class Profile extends Auditable<String> implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
  @Transient
  private ProfileTypeEnum profileTypeEnum;

  // Mức độ
  @Basic
  private Integer priority;
  @Transient
  private ProfilePriorityEnum priorityValue;

  // Trạng thái hồ sơ
  @Basic
  private Integer state;
  @Transient
  private ProfileStateEnum stateValue;

  // Trạng thái hồ sơ đăng ký
  @Column(name="profile_process_state")
  private Integer profileProcessState;


  // Đánh giá
  @Column(name = "review")
  private Integer review;

  // Đánh giá
  @Column(name = "review_note")
  private String reviewNote;

  // Đánh giá
  @Column(name = "note")
  private String note;

  // Gửi email
  @Column(name = "notify_by_email", columnDefinition = "BIT")
  private Boolean notifyByEmail;

  @Column(name = "additional_time")
  private Integer additionalTime; // Số lượng ủy nhiệm chi

  @Column(name = "others_profile")
  private String othersProfile; // Số lượng ủy nhiệm chi


  @PostLoad
  void fillTransient() {
    if (priority != null) {
      this.priorityValue = ProfilePriorityEnum.of(priority);
    }

    if (type != null) {
      this.profileTypeEnum = ProfileTypeEnum.of(type);
    }
    if (state != null) {
      this.stateValue = ProfileStateEnum.of(state);
    }
  }

  @PrePersist
  void fillPersistent() {
    if (priorityValue != null) {
      this.priority = priorityValue.getPriority();
    }

    if (profileTypeEnum != null) {
      this.type = profileTypeEnum.getType();
    }

    if (stateValue != null) {
      this.state = stateValue.getValue();
    }
  }

}

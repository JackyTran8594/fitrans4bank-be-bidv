package com.eztech.fitrans.model;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(name = Constants.ResultSetMapping.PROFILE_HISTORY_DTO, classes = {
    @ConstructorResult(targetClass = ProfileHistoryDTO.class, columns = {
        @ColumnResult(name = "id", type = Long.class),
        @ColumnResult(name = "profile_id", type = Long.class),
        @ColumnResult(name = "staff_id", type = Long.class),
        @ColumnResult(name = "time_received", type = LocalDateTime.class),
        @ColumnResult(name = "standard_time", type = LocalDateTime.class),
        @ColumnResult(name = "created_by", type = String.class),
        @ColumnResult(name = "created_date", type = LocalDateTime.class),
        @ColumnResult(name = "last_updated_by", type = String.class),
        @ColumnResult(name = "last_updated_date", type = LocalDateTime.class),
        @ColumnResult(name = "status", type = String.class),
        @ColumnResult(name = "state", type = Integer.class),
        @ColumnResult(name = "staff_name", type = String.class),

    })
})
public class ProfileHistory extends Auditable<String> implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "profile_id")
  private Long profileId;

  @Column(name = "staff_id")
  private Long staffId; // Cán bộ đang thực hiện

  @Column(name = "time_received")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime timeReceived; // Ngày bàn giao QTTD - CM: Credit Management

  @Column(name = "standard_time")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime standardTime;

  // Trạng thái hồ sơ
  @Basic
  private Integer state;
  @Transient
  private ProfileStateEnum stateValue;

  @PostLoad
  void fillTransient() {

    if (state != null) {
      this.stateValue = ProfileStateEnum.of(state);
    }
  }

  @PrePersist
  void fillPersistent() {

    if (stateValue != null) {
      this.state = stateValue.getValue();
    }
  }

}

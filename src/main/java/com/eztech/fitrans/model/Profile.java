package com.eztech.fitrans.model;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.ProfilePriorityEnum;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.constants.ProfileTypeEnum;
import com.eztech.fitrans.dto.response.ProfileDTO;
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
@SqlResultSetMapping(
    name = Constants.ResultSetMapping.PROFILE_DTO,
    classes = {
        @ConstructorResult(
            targetClass = ProfileDTO.class,
            columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "customer_id", type = Long.class),
                @ColumnResult(name = "staff_id", type = Long.class),
                @ColumnResult(name = "type", type = Integer.class),
                @ColumnResult(name = "priority", type = Integer.class),
                @ColumnResult(name = "state", type = Integer.class),
                @ColumnResult(name = "process_date", type = LocalDateTime.class),
                @ColumnResult(name = "last_updated_by", type = String.class),
                @ColumnResult(name = "last_updated_date", type = LocalDateTime.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "cif", type = String.class),
                @ColumnResult(name = "customer_name", type = String.class),
                @ColumnResult(name = "staff_name", type = String.class)
            }
        )
    }
)
public class Profile extends Auditable<String> implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "customer_id")
  private Long customerid;
  @Column(name = "staff_id")
  private Long staffId;   //Cán bộ đang thực hiện

  @Column(name = "process_date")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  protected LocalDateTime processDate;    //Ngày phát sinh

  //Loai giao dich
  @Basic
  private Integer type;
  @Transient
  private ProfileTypeEnum profileTypeEnum;

  //Mức độ
  @Basic
  private Integer priority;
  @Transient
  private ProfilePriorityEnum priorityValue;

  //Trạng thái hồ sơ
  @Basic
  private Integer state;
  @Transient
  private ProfileStateEnum stateValue;

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

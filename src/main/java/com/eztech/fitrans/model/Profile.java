package com.eztech.fitrans.model;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
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
                @ColumnResult(name = "type", type = String.class),
                @ColumnResult(name = "priority", type = String.class),
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
  private String type;    //Loai giao dich
  private String priority;    //Mức độ
  @Column(name = "staff_id")
  private Long staffId;   //Cán bộ đang thực hiện

  @Column(name = "process_date")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  protected LocalDateTime processDate;    //Ngày phát sinh
}

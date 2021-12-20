package com.eztech.fitrans.dto.response;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO implements Serializable {

  private Long id;
  private Long customerid;
  private String customerName;
  private String cif;
  private String type;    //Loai giao dich
  private String priority;    //Mức độ
  private Long staffId;   //Cán bộ đang thực hiện
  private String staffName;   //Cán bộ đang thực hiện

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  protected LocalDateTime processDate;    //Ngày phát sinh

  private String createdBy;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdDate;
  private String lastUpdatedBy;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime lastUpdatedDate;
  private String status;

  public ProfileDTO(Long id, Long customerid, Long staffId, String type,
      String priority, LocalDateTime processDate,
      String lastUpdatedBy, LocalDateTime lastUpdatedDate, String status, String cif, String customerName,
      String staffName) {
    this.id = id;
    this.customerid = customerid;
    this.cif = cif;
    this.customerName = customerName;
    this.type = type;
    this.priority = priority;
    this.staffId = staffId;
    this.staffName = staffName;
    this.processDate = processDate;
    this.lastUpdatedBy = lastUpdatedBy;
    this.lastUpdatedDate = lastUpdatedDate;
    this.status = status;
  }
}

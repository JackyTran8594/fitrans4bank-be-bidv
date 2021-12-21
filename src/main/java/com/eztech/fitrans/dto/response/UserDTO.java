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
public class UserDTO implements Serializable {

  private Long id;
  private String username;
  private String email;
  private String fullName;
  private Long departmentId;
  private String departmentName;
  private String position;
  private String createdBy;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdDate;
  private String lastUpdatedBy;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime lastUpdatedDate;
  private String status;


  public UserDTO(Long id, String username, String email, String fullName,
      String position, Long departmentId, String status,
      String lastUpdatedBy, LocalDateTime lastUpdatedDate, String departmentName) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.fullName = fullName;
    this.departmentId = departmentId;
    this.departmentName = departmentName;
    this.position = position;
    this.lastUpdatedBy = lastUpdatedBy;
    this.lastUpdatedDate = lastUpdatedDate;
    this.status = status;
  }
}
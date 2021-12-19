package com.eztech.fitrans.model;

import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Class common Auditable
 *
 * @author HoangTD5
 * @version 0.1
 * @date 8/17/2020
 */
@MappedSuperclass
@Data
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public abstract class Auditable<U> implements Serializable {

  @CreatedBy
  @Column(name = "created_by")
  protected U createdBy;

  @CreatedDate
  @Column(name = "created_date")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  protected LocalDateTime createdDate;

  @LastModifiedBy
  @Column(name = "last_updated_by")
  protected U lastUpdatedBy;

  @LastModifiedDate
  @Column(name = "last_updated_date")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  protected LocalDateTime lastUpdatedDate;

  @Column(name = "status")
  protected String status;
}

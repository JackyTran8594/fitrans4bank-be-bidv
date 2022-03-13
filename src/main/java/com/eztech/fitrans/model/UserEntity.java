package com.eztech.fitrans.model;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.UserDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@SqlResultSetMapping(
		name = Constants.ResultSetMapping.USER_ENTITY_DTO,
		classes = {
				@ConstructorResult(
						targetClass = UserDTO.class,
						columns = {
								@ColumnResult(name = "id", type = Long.class),
								@ColumnResult(name = "username", type = String.class),
								@ColumnResult(name = "email", type = String.class),
								@ColumnResult(name = "full_name", type = String.class),
								@ColumnResult(name = "position", type = String.class),
								@ColumnResult(name = "department_id", type = Long.class),
								@ColumnResult(name = "status", type = String.class),
								@ColumnResult(name = "last_updated_by", type = String.class),
								@ColumnResult(name = "last_updated_date", type = LocalDateTime.class),
								@ColumnResult(name = "code", type = String.class),
								@ColumnResult(name = "name", type = String.class),
								@ColumnResult(name = "role_id", type = Long.class),
								@ColumnResult(name = "role_name", type = String.class),
								@ColumnResult(name = "phone_number", type = String.class)
						}
				)
		}
)
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "`username`")
	private String username;
	@Column(name = "`password`")
	private String password;
	@Column(name = "`full_name`")
	private String fullName;
	@Column(name = "`phoneNumber`")
	private String phoneNumber;
	@Column(name = "`department_id`")
	private Long departmentId;	//phong ban
	@Column(name = "`position`")
	private String position;	//Chuc vu
	@Column(name = "`email`")
	private String email;	//Chuc vu
	// bi-directional many-to-many association to Role
//	@ManyToMany
//	@JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
//	private Set<Role> roles;

	/**
	 * @param username
	 * @param password
	 */
	public UserEntity(String username, String password) {
		this.username = username;
		this.password = password;
	}

	//Add
	@Column(name = "`created_by`")
	private String createdBy;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Column(name = "`created_date`")
	private LocalDateTime createdDate;
	@Column(name = "`last_updated_by`")
	private String lastUpdatedBy;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Column(name = "`last_updated_date`")
	private LocalDateTime lastUpdatedDate;
	@Column(name = "`status`")
	private String status;
}

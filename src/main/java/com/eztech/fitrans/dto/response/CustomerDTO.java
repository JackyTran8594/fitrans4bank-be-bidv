package com.eztech.fitrans.dto.response;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.eztech.fitrans.constants.CustomerTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO extends BaseImportDTO implements Serializable {

    private Long id;
    private String cif;
    private String name;
    private String address;
    private String tel;
    //Loai khach hang (VIP, THONG THUONG)
    private Integer type;
    private String typeName;

    private String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdDate;
    private String lastUpdatedBy;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdatedDate;
    private String status;

    //For import excel
    private Integer stt;
    private String errorMsg;
    private Boolean error = false;

    public void fillTransient() {
        if (type != null) {
            this.typeName = CustomerTypeEnum.of(type).getName();
        }
    }
}

package com.eztech.fitrans.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class BaseImportDTO implements Serializable {

    //For import excel
    private Integer stt;
    private String errorMsg;
    private Boolean error = false;

}

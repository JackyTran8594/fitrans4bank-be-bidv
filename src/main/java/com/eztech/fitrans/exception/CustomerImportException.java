package com.eztech.fitrans.exception;

import com.eztech.fitrans.dto.response.BaseImportDTO;
import com.eztech.fitrans.dto.response.CustomerDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class CustomerImportException extends RuntimeException {

    private final List<CustomerDTO> dtoList;

    public CustomerImportException(String message, List<CustomerDTO> dtoList) {
        super(message);
        this.dtoList = dtoList;
    }
}

package com.eztech.fitrans.exception;


import com.eztech.fitrans.dto.response.ErrorCodeEnum;

/**
 * Validate dữ liệu đầu vào
 */
public class InputInvalidException extends ApplicationException {

  public InputInvalidException(ErrorCodeEnum code, Object... args) {
    super(code, args);
  }
}

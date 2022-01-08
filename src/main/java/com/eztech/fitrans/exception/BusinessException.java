package com.eztech.fitrans.exception;

import com.eztech.fitrans.dto.response.ErrorCodeEnum;

public class BusinessException extends ApplicationException {

  public BusinessException(ErrorCodeEnum code, Object... args) {
    super(code, args);
  }
}

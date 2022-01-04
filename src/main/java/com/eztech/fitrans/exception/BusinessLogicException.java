package com.eztech.fitrans.exception;

import com.eztech.fitrans.dto.response.ErrorCodeEnum;

public class BusinessLogicException extends ApplicationException {

  public BusinessLogicException(ErrorCodeEnum code, Object... args) {
    super(code, args);
  }
}

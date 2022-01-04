package com.eztech.fitrans.exception;


import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.locale.Translator;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

  private final ErrorCodeEnum code;
  private final transient Object[] args;

  public ApplicationException(ErrorCodeEnum code, Object... args) {
    super(Translator.toMessage(code, args));
    this.code = code;
    this.args = args;
  }
}

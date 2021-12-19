/*
 * Copyright (C) 2020 Viettel Digital Services. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.eztech.fitrans.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * MessageCode
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Getter
@Builder
public class ErrorMessageDTO implements Serializable {

  /**
   * Code của  message
   */
  protected ErrorCodeEnum code;
  /**
   * Message
   */
  protected String message;

  /**
   * @param code
   * @param message
   */
  public ErrorMessageDTO(ErrorCodeEnum code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * @param code
   */
  public ErrorMessageDTO(ErrorCodeEnum code) {
    this(code, "");
  }

}

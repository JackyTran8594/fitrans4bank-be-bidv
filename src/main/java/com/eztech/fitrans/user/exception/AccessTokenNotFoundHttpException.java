package com.eztech.fitrans.user.exception;

import com.eztech.fitrans.exception.HttpException;
import org.springframework.http.HttpStatus;

public class AccessTokenNotFoundHttpException extends HttpException {

    public AccessTokenNotFoundHttpException(String message, HttpStatus status) {
        super(message, status);
    }
}

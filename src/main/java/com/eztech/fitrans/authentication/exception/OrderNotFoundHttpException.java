package com.eztech.fitrans.authentication.exception;

import com.eztech.fitrans.exception.HttpException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundHttpException extends HttpException {
    private static final long serialVersionUID = 4770986620665158856L;

    public OrderNotFoundHttpException(String message, HttpStatus status) {
        super(message, status);
    }
}



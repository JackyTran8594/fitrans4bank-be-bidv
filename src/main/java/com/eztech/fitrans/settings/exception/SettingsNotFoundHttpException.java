
package com.eztech.fitrans.settings.exception;

import com.eztech.fitrans.exception.HttpException;
import org.springframework.http.HttpStatus;

public class SettingsNotFoundHttpException extends HttpException {

    private static final long serialVersionUID = -5258959358527382145L;

    public SettingsNotFoundHttpException(String message, HttpStatus status) {
        super(message, status);
    }
}

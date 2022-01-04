package com.eztech.fitrans.exception;

import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.dto.response.ErrorMessageDTO;
import com.eztech.fitrans.util.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

    public static final String SPREADSHEETML_SHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String ATTACHMENT_FILENAME_ERROR_XLSX = "attachment; filename=error.xlsx";

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDTO defaultExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(ErrorCodeEnum.ER0000, e.getMessage());
    }

    @ExceptionHandler(value = ApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDTO applicationExceptionHandler(ApplicationException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(ErrorCodeEnum.ER0000, e.getArgs());
    }

    @ExceptionHandler(value = BusinessLogicException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDTO businessLogicExceptionHandler(BusinessLogicException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(e.getCode(), e.getArgs());
    }

    @ExceptionHandler(value = InputInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO inputInvalidExceptionHandler(InputInvalidException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(e.getCode(), e.getArgs());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO defaultHttpMessageNotReadableExceptionHandler(
            HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(ErrorCodeEnum.ER0001, e.getMessage());
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO defaultHttpMediaTypeNotSupportedExceptionHandler(
            HttpMediaTypeNotSupportedException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(ErrorCodeEnum.ER0001, e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO defaultMethodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(ErrorCodeEnum.ER0001, e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO defaultMethodArgumentTypeMismatchExceptionHandler(
            MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(ErrorCodeEnum.ER0001, e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageDTO handleError404(HttpServletRequest request, Exception e) {
        log.error(String.format("Message {%s} - URL: {%s}", e.getMessage(), request.getRequestURL()), e);
        return ResponseFactory.error(ErrorCodeEnum.ER0004);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDTO handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(ErrorCodeEnum.ER0000, e.getMessage());
    }
}

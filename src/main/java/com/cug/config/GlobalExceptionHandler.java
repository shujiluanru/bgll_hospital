package com.cug.config;

import com.cug.constant.AuthConstant;
import com.cug.constant.CommonConstant;
import com.cug.domain.pojo.R;
import com.cug.exception.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(ValidationException.class)
    public R handleValidationException(ValidationException e) {
        return new R("401", AuthConstant.TOKEN_AUTHORITIES_FAIL,null);
    }
    @ExceptionHandler(SmsException.class)
    public R handleSmsException(SmsException e) {
        return new R("500", AuthConstant.SEND_SMS_FAIL,null);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public R handleResourceNotFoundException(ResourceNotFoundException e) {
        return new R("404", e.getMessage(),null);
    }
    @ExceptionHandler(ServerException.class)
    public R handleServerException(ServerException e) {
        return new R("500", e.getMessage(),null);
    }
    @ExceptionHandler(ParamException.class)
    public R handleParamException(ParamException e) {
        return new R("400", e.getMessage(),null);
    }
}

package com.csgo.exception;


import com.csgo.support.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalAdviceHandle {

    @ExceptionHandler(AdminErrorException.class)
    public Result adminErrorExceptionHandle(AdminErrorException adminErrorException) {
        return new Result().fairResult(adminErrorException.getMessage());
    }
}

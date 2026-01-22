package com.example.test.config;

import com.example.test.utils.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.Annotation;

@RestControllerAdvice
public class GlobleExceptionHandler{
    @ExceptionHandler(Throwable.class)
   public R handleException(Throwable e){
        e.printStackTrace();
        return R.error(e.getMessage());
   }
}

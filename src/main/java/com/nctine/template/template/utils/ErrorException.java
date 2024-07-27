package com.nctine.template.template.utils;

import com.nctine.template.template.model.ErrorResponse;
import com.nctine.template.template.model.ErrorTypeBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorException extends Exception {
    private String code;
    private HttpStatus httpStatus;
    private List<ErrorResponse> errors = new ArrayList();

    public ErrorException(ErrorResponse error, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.errors.add(error);
    }

    public ErrorException(List<ErrorResponse> errors, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.errors = errors;
    }

    public ErrorException(ErrorTypeBase errorType, HttpStatus httpStatus) {
        super(errorType.getMessage());
        this.code = errorType.getCode();
        this.httpStatus = httpStatus;
        this.errors.add(new ErrorResponse(errorType.getCode(), errorType.getMessage()));
    }

    public ErrorException(ErrorTypeBase errorType, HttpStatus httpStatus, Object... args) {
        super(errorType.getMessage());
        this.code = errorType.getCode();
        this.httpStatus = httpStatus;
        this.errors.add(new ErrorResponse(errorType.getCode(), String.format(errorType.getMessage(), args)));
    }
}
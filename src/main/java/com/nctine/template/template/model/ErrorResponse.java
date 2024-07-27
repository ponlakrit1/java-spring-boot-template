package com.nctine.template.template.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String code;
    private String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(ErrorTypeBase errorType) {
        this.code = errorType.getCode();
        this.message = errorType.getMessage();
    }

    public ErrorResponse(ErrorTypeBase errorType, Object... str) {
        this.code = errorType.getCode();
        this.message = String.format(errorType.getMessage(), str);
    }

    public ErrorResponse() {
    }
}

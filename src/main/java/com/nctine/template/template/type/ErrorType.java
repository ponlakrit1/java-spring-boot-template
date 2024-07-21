package com.nctine.template.template.type;

import com.nctine.template.template.model.ErrorTypeBase;
import lombok.Getter;

@Getter
public enum ErrorType implements ErrorTypeBase {
    USER_ALREADY_EXISTS					                        	("40901", "user already exists"),

    ;

    private String code;
    private String message;

    ErrorType(String code, String message){
        this.code = code;
        this.message = message;
    }
}

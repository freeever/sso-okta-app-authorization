package com.dxu.sso.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SsoApplicationException extends RuntimeException {

    private final int statusCode;

    public SsoApplicationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}

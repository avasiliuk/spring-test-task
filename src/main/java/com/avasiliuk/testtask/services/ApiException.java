package com.avasiliuk.testtask.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApiException extends RuntimeException {
    private String message;
    private HttpStatus code;

    public ApiException(final HttpStatus code, final String message, Object... msgParams) {
        this.code = code;
        this.message = String.format(message, msgParams);
    }
}

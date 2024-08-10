package com.zerobase.memberapi.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenException extends RuntimeException {

    private ErrorCode errorCode;
    private String errorMessage;

    public TokenException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
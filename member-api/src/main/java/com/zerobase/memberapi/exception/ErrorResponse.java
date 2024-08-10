package com.zerobase.memberapi.exception;

import lombok.*;

import java.util.Objects;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ErrorResponse {
    // 예외 발생한 경우 예외 정보 전달 위해
    private ErrorCode errorCode;
    private String errorMessage;

    public static ErrorResponse from(MemberException e) {
        return ErrorResponse.builder()
                .errorMessage(e.getErrorMessage())
                .errorCode(e.getErrorCode())
                .build();
    }
}
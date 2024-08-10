package com.zerobase.memberapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    // 회원가입
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다."),

    // 로그인, 유저정보 가져오기
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "일치하는 회원이 없습니다."),
    LOGIN_CHECK_FAIL(HttpStatus.BAD_REQUEST, "이메일과 패스워드를 확인해주세요.");


    private final HttpStatus httpStatus;
    private final String description;
}
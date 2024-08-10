package com.zerobase.storeapi.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    // 매장 등록
    DUPLICATE_STORE_NAME(HttpStatus.BAD_REQUEST, "매장명은 중복일 수 없습니다."),
    CHECK_STORE_HOURS(HttpStatus.BAD_REQUEST, "매장 운영시간을 확인해주세요."),
    CHECK_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "예약 시작시간과 마감시간을 확인해주세요."),

    // 매장 수정
    NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "매장명이 존재하지 않습니다."),
    NOT_FOUND_RESERVATION_INFO(HttpStatus.BAD_REQUEST, "매장 예약 상세정보가 존재하지 않습니다."),
    CANNOT_UPDATE_INFO(HttpStatus.BAD_REQUEST, "예약이 열려있지 않은 날짜입니다."),

    // 매장 삭제
    STILL_HAVE_RESERVATION(HttpStatus.BAD_REQUEST, "해당 매장에 예약이 남아 있습니다."),
    ALREADY_DELETED_STORE(HttpStatus.BAD_REQUEST, "이미 삭제된 매장입니다."),

    // 매장 검색
    UNMATCHED_PARTNER_STORE(HttpStatus.BAD_REQUEST, "매장 정보와 파트너 정보가 일치하지 않습니다.");



    private final HttpStatus httpStatus;
    private final String description;
}
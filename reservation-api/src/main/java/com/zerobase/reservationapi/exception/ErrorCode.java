package com.zerobase.reservationapi.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),


    ALREADY_DELETED_STORE(HttpStatus.BAD_REQUEST, "이미 삭제된 매장입니다."),

    // 매장 예약
    LOWER_STORE_MIN_CAPACITY(HttpStatus.BAD_REQUEST, "예약 가능 인원이 부족합니다."),
    OVER_STORE_MAX_CAPACITY(HttpStatus.BAD_REQUEST, "예약 가능인원을 초과하였습니다."),
    RESERVATION_CLOSED(HttpStatus.BAD_REQUEST, "예약이 마감되었습니다."),
    ALREADY_MAKE_RESERVATION(HttpStatus.BAD_REQUEST, "예약정보가 존재합니다."),
    ALREADY_CHANGE_STATUS(HttpStatus.BAD_REQUEST, "이미 예약상태가 변경되었습니다."),
    CANNOT_RESERVATION_DATE(HttpStatus.BAD_REQUEST, "예약 가능한 날짜가 아닙니다."),

    // 매장 예약 확인
    UNMATCHED_MEMBER_RESERVATION(HttpStatus.BAD_REQUEST, "예약 정보와 고객 정보가 일치하지 않습니다."),
    OVER_RESERVATION_COUNT(HttpStatus.BAD_REQUEST, "예약 가능 인원을 초과합니다."),

    // 예약 수락,거절
    NOT_FOUND_RESERVATION(HttpStatus.BAD_REQUEST, "예약 정보가 존재하지 않습니다."),

    // 매장 방문 확인
    CHECK_RESERVATION_STATUS(HttpStatus.BAD_REQUEST, "예약 수락을 확인해주세요."),
    CANNOT_CHECK_YET(HttpStatus.BAD_REQUEST, "예약 확인은 10분전부터 가능합니다."),
    NOT_TODAY_RESERVATION(HttpStatus.BAD_REQUEST, "예약 확인은 10분전부터 가능합니다."),
    OVER_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "예약 시간이 지났습니다."),

    // 리뷰 작성
    VISIT_NOT_TRUE(HttpStatus.BAD_REQUEST, "방문 정보가 존재하지 않습니다."),
    UNMATCHED_CUSTOMER_RESERVATION(HttpStatus.BAD_REQUEST, "고객 정보와 예약 정보가 일치하지 않습니다."),
    OVER_RATING_LIMIT(HttpStatus.BAD_REQUEST, "별점은 최대 5점까지 가능합니다."),
    ALREADY_CREATED_REVIEW(HttpStatus.BAD_REQUEST, "이미 리뷰를 작성하였습니다."),

    // 리뷰 수정
    UNMATCHED_CUSTOMER_REVIEW(HttpStatus.BAD_REQUEST, "리뷰 작성자와 매장 관리자만 삭제가능합니다."),
    UNMATCHED_PARTNER_REVIEW(HttpStatus.BAD_REQUEST, "리뷰 작성자와 매장 관리자만 삭제가능합니다."),

    // 리뷰 삭제
    NOT_MATCH_PARTNER(HttpStatus.BAD_REQUEST, "파트너 정보와 예약 정보가 일치하지 않습니다.");


    private final HttpStatus httpStatus;
    private final String description;
}
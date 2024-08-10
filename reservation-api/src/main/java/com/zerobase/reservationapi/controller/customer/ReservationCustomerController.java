package com.zerobase.reservationapi.controller.customer;

import com.zerobase.reservationapi.client.MemberClient;
import com.zerobase.reservationapi.controller.ResponseError;
import com.zerobase.reservationapi.controller.ValidationErrorResponse;
import com.zerobase.reservationapi.domain.reservation.form.MakeReservation;
import com.zerobase.reservationapi.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation/customer")
public class ReservationCustomerController {
    private final ReservationService reservationService;
    private final ValidationErrorResponse validationErrorResponse;
    private final MemberClient memberClient;

    /**
     * 매장 예약
     *
     * @param token
     * @param form   : reservationInfoId, headCount(예약 인원), phone, reservationDate
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 저장된 예약 정보
     */
    @PostMapping
    public ResponseEntity<?> makeReservation(@RequestHeader(name = "Authorization") String token,
                                             @RequestBody @Valid MakeReservation form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(reservationService.makeReservation(memberClient.getMemberId(token), form));
    }


    /**
     * 매장 예약 취소
     *
     * @param token
     * @param id
     * @return : 취소한 예약 정보
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@RequestHeader(name = "Authorization") String token,
                                               @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(memberClient.getMemberId(token), id));
    }

    /**
     * 매장 방문 확인
     *
     * @param token
     * @param id
     * @return : 수정한 예약 정보
     */
    @PatchMapping("/visit")
    public ResponseEntity<?> visitReservation(@RequestHeader(name = "Authorization") String token,
                                              @RequestParam Long id) {
        return ResponseEntity.ok(reservationService.visitReservation(memberClient.getMemberId(token), id));
    }


}
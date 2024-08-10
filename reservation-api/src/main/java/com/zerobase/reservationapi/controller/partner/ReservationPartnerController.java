package com.zerobase.reservationapi.controller.partner;


import com.zerobase.reservationapi.client.MemberClient;
import com.zerobase.reservationapi.controller.ResponseError;
import com.zerobase.reservationapi.controller.ValidationErrorResponse;
import com.zerobase.reservationapi.domain.reservation.form.ConfirmReservation;
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
@RequestMapping("/api/reservation/partner")
public class ReservationPartnerController {
    private final ReservationService reservationService;
    private final ValidationErrorResponse validationErrorResponse;
    private final MemberClient memberClient;

    /**
     * 신청된 예약 승인, 거절
     *
     * @param token
     * @param form   : reservationId, status(예약/승인)
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 수정한 예약 정보
     */
    @PatchMapping
    public ResponseEntity<?> changeReservationStatus(@RequestHeader(name = "Authorization") String token,
                                                     @RequestBody @Valid ConfirmReservation form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(reservationService.changeReservationStatus(memberClient.getMemberId(token), form));
    }


}
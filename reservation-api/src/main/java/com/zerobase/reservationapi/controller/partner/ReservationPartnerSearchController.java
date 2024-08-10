package com.zerobase.reservationapi.controller.partner;


import com.zerobase.reservationapi.client.MemberClient;
import com.zerobase.reservationapi.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation/partner/search")
public class ReservationPartnerSearchController {
    private final ReservationService reservationService;
    private final MemberClient memberClient;

    /**
     * 파트너가 자신의 특정 매장 예약 리스트 날짜별 확인
     *
     * @param token
     * @param storeId
     * @param date
     * @param pageable
     * @return 예약 리스트
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<?> searchReservationByPartner(@RequestHeader(name = "Authorization") String token,
                                                        @PathVariable Long storeId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                        Pageable pageable) {

        return ResponseEntity.ok(reservationService.searchReservationByPartner(memberClient.getMemberId(token), storeId, date, pageable));
    }
}
package com.zerobase.reservationapi.controller.customer;


import com.zerobase.reservationapi.client.MemberClient;
import com.zerobase.reservationapi.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation/customer/search")
public class ReservationCustomerSearchController {
    private final ReservationService reservationService;
    private final MemberClient memberClient;

    /**
     * 고객이 자신이 예약한 리스트 확인
     *
     * @param token
     * @param pageable
     * @return 예약 리스트
     */
    @GetMapping
    public ResponseEntity<?> searchReservationByCustomer(@RequestHeader(name = "Authorization") String token
            , Pageable pageable) {

        return ResponseEntity.ok(reservationService.searchReservationByMember(memberClient.getMemberId(token), pageable));
    }

    /**
     * 고객이 특정 매장에 예약한 리스트 확인
     *
     * @param token
     * @param storeId
     * @param pageable
     * @return 예약 리스트
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<?> searchReservationByCustomerWithStore(@RequestHeader(name = "Authorization") String token,
                                                                  @PathVariable Long storeId, Pageable pageable) {

        return ResponseEntity.ok(reservationService.searchReservationByMemberWithStore(memberClient.getMemberId(token), storeId, pageable));
    }


}
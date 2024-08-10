package com.zerobase.reservationapi.controller.partner;


import com.zerobase.reservationapi.client.MemberClient;
import com.zerobase.reservationapi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation/partner/review")
public class ReviewPartnerController {

    private final ReviewService reviewService;
    private final MemberClient memberClient;

    /**
     * 파트너가 자신의 매장에 등록된 리뷰 삭제
     *
     * @param token
     * @param id
     * @return : 삭제한 id + 삭제되었습니다.
     */
    @DeleteMapping
    public ResponseEntity<?> deleteReviewByPartner(@RequestHeader(name = "Authorization") String token,
                                                   @RequestParam Long id) {

        return ResponseEntity.ok(reviewService.deleteReviewByPartner(memberClient.getMemberId(token), id));
    }


    /**
     * 파트너의 특정 매장에 등록된 모든 리뷰
     *
     * @param token
     * @param storeId
     * @param pageable
     * @return : 등록된 리뷰 리스트
     */
    @GetMapping("/search/partner")
    public ResponseEntity<?> searchReviewByStore(@RequestHeader(name = "Authorization") String token,
                                                 @RequestParam Long storeId, final Pageable pageable) {

        return ResponseEntity.ok(reviewService.searchReviewByStore(memberClient.getMemberId(token), storeId, pageable));
    }

}
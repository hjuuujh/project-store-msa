package com.zerobase.reservationapi.controller.customer;


import com.zerobase.reservationapi.client.MemberClient;
import com.zerobase.reservationapi.controller.ResponseError;
import com.zerobase.reservationapi.controller.ValidationErrorResponse;
import com.zerobase.reservationapi.domain.review.form.CreateReview;
import com.zerobase.reservationapi.domain.review.form.UpdateReview;
import com.zerobase.reservationapi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation/customer/review")
public class ReviewCustomerController {

    private final ReviewService reviewService;
    private final ValidationErrorResponse validationErrorResponse;
    private final MemberClient memberClient;

    /**
     * 리뷰 등록
     *
     * @param token
     * @param form   : reservationId, rating, comment
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 등록한 리뷰 정보
     */
    @PostMapping
    public ResponseEntity<?> createReview(@RequestHeader(name = "Authorization") String token,
                                          @RequestBody @Valid CreateReview form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(reviewService.createReview(memberClient.getMemberId(token), form));
    }

    /**
     * 리뷰 수정
     *
     * @param token
     * @param form   : id, rating, comment
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 수정한 리뷰 정보
     */
    @PatchMapping
    public ResponseEntity<?> updateReview(@RequestHeader(name = "Authorization") String token,
                                          @RequestBody @Valid UpdateReview form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(reviewService.updateReview(memberClient.getMemberId(token), form));
    }

    /**
     * 고객이 자신이 등록한 리뷰 삭제
     *
     * @param token
     * @param id
     * @return : 삭제한 id + 삭제되었습니다.
     */
    @DeleteMapping
    public ResponseEntity<?> deleteReviewByCustomer(@RequestHeader(name = "Authorization") String token,
                                                    @RequestParam Long id) {

        return ResponseEntity.ok(reviewService.deleteReviewByCustomer(memberClient.getMemberId(token), id));
    }

    /**
     * 고객이 등록한 모든 리뷰 확인
     *
     * @param token
     * @param pageable
     * @return : 등록한 리뷰 리스트
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchReview(@RequestHeader(name = "Authorization") String token,
                                          final Pageable pageable) {

        return ResponseEntity.ok(reviewService.searchReview(memberClient.getMemberId(token), pageable));
    }


}
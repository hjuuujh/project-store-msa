package com.zerobase.reservationapi.service;

import com.zerobase.reservationapi.client.StoreClient;
import com.zerobase.reservationapi.client.from.ChangeRatingForm;
import com.zerobase.reservationapi.domain.reservation.entity.Reservation;
import com.zerobase.reservationapi.domain.review.dto.ReviewDto;
import com.zerobase.reservationapi.domain.review.entity.Review;
import com.zerobase.reservationapi.domain.review.form.CreateReview;
import com.zerobase.reservationapi.domain.review.form.UpdateReview;
import com.zerobase.reservationapi.domain.store.StoreDto;
import com.zerobase.reservationapi.exception.ReservationException;
import com.zerobase.reservationapi.exception.ReviewException;
import com.zerobase.reservationapi.repository.ReservationRepository;
import com.zerobase.reservationapi.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.zerobase.reservationapi.exception.ErrorCode.*;


@Service
@Slf4j
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreClient storeClient;

    /**
     * 리뷰 등록
     *
     * @param customerId
     * @param form       : reservationId, rating, comment
     * @return : 등록한 리뷰 정보
     */
    @Transactional
    public ReviewDto createReview(Long customerId, CreateReview form) {
        // 리뷰 등록 요청한 고객의 예약이 맞는지 확인
        Reservation reservation = reservationRepository.findByIdAndCustomerId(form.getReservationId(), customerId)
                .orElseThrow(() -> new ReservationException(UNMATCHED_MEMBER_RESERVATION));

        checkCustomerVisited(reservation);
        checkRatingLimit(form.getRating());
        checkReviewAlreadyCreated(customerId, form.getReservationId());

        // 리뷰 등록할 매장 찾음
        StoreDto storeDto = storeClient.getStore(reservation.getStoreId());

        Review review = Review.of(customerId, form, reservation);
        reviewRepository.save(review);

        // 등록 요청의 별점으로 매장의 별점 업데이트
        ChangeRatingForm request = ChangeRatingForm.builder()
                .storeId(reservation.getStoreId())
                .rating(form.getRating()).build();
        storeClient.changeRating(request);

        return ReviewDto.from(review);
    }

    /**
     * 이미 해당 예약에 리뷰를 등록했는지 확인
     *
     * @param customerId
     * @param reservationId
     */
    private void checkReviewAlreadyCreated(Long customerId, Long reservationId) {
        // 등록 요청 고객이 해당예약에 이미 리뷰 등록한 경우 예외 발생 : ALREADY_CREATED_REVIEW "이미 리뷰를 작성하였습니다."
        boolean exists = reviewRepository.existsByCustomerIdAndReservationId(customerId, reservationId);
        if (exists)
            throw new ReviewException(ALREADY_CREATED_REVIEW);
    }

    /**
     * 리뷰 만점인 5점보다 높은 별점을 줬는지 확인
     *
     * @param rating
     */
    private void checkRatingLimit(float rating) {
        // 5점 보다 높은 별점을 준 경우 예외 발생 : OVER_RATING_LIMIT "별점은 최대 5점까지 가능합니다."
        if (rating > 5) {
            throw new ReviewException(OVER_RATING_LIMIT);
        }
    }

    /**
     * 방문하지 않은 예약에 리뷰를 작성하려고 하는지 확인
     *
     * @param reservation
     */
    private void checkCustomerVisited(Reservation reservation) {
        // 방문하지 않은 예약에 리뷰를 등록하려고 하는 경우 예외 발생 : VISIT_NOT_TRUE "방문 정보가 존재하지 않습니다."
        if (!reservation.isVisit())
            throw new ReviewException(VISIT_NOT_TRUE);
    }

    /**
     * 리뷰 수정
     *
     * @param customerId
     * @param form       : id, rating, comment
     * @return : 수정한 리뷰 정보
     * //
     */
    @Transactional
    public ReviewDto updateReview(Long customerId, UpdateReview form) {
        // 본인이 등록한 리뷰가 맞는지 확인
        Review review = reviewRepository.findByIdAndCustomerId(form.getId(), customerId)
                .orElseThrow(() -> new ReservationException(UNMATCHED_CUSTOMER_REVIEW));

        checkRatingLimit(form.getRating());

        // 리뷰 등록할 매장 찾음
        StoreDto storeDto = storeClient.getStore(review.getStoreId());

        // 새로 요청한 별점으로 매장의 별점 수정
        ChangeRatingForm request = ChangeRatingForm.builder()
                .storeId(review.getStoreId())
                .rating(form.getRating()).build();
        storeClient.changeRating(request);

        review.update(form);

        return ReviewDto.from(review);
    }

    /**
     * 고객이 자신이 등록한 리뷰 삭제
     *
     * @param userId
     * @param id
     * @return : 삭제한 id + 삭제되었습니다.
     */
    @Transactional
    public String deleteReviewByCustomer(Long userId, Long id) {
        // 본인이 등록한 리뷰가 아닌 경우 예외 발생 : UNMATCHED_CUSTOMER_REVIEW "리뷰 작성자와 매장 관리자만 삭제가능합니다."
        Review review = reviewRepository.findByIdAndCustomerId(id, userId)
                .orElseThrow(() -> new ReviewException(UNMATCHED_CUSTOMER_REVIEW));

        // 리뷰 등록할 매장 찾음
        // 삭제할 리뷰의 별점으로 매장의 별점 수정
        cancelStore(review);
        reviewRepository.delete(review);
        return String.format("%d 리뷰 삭제", review.getId());
    }

    /**
     * 매장의 별점 수정
     *
     * @param review
     */
    @Transactional
    public void cancelStore(Review review) {
        // 삭제할 리뷰의 별점으로 매장의 별점 수정
        ChangeRatingForm request = ChangeRatingForm.builder()
                .storeId(review.getStoreId())
                .rating(review.getRating()).build();

        storeClient.decreaseRating(request);
    }


    /**
     * 파트너가 자신의 매장의 리뷰 삭제
     *
     * @param userId
     * @param id
     * @return : 삭제한 id + 삭제되었습니다.
     */
    @Transactional
    public String deleteReviewByPartner(Long userId, Long id) {
        // 본인 매장의 리뷰가 아닌 경우 예외 발생 : UNMATCHED_CUSTOMER_REVIEW "리뷰 작성자와 매장 관리자만 삭제가능합니다."
        Review review = reviewRepository.findByIdAndPartnerId(id, userId)
                .orElseThrow(() -> new ReviewException(UNMATCHED_PARTNER_REVIEW, "리뷰 작성자와 매장 관리자만 삭제가능합니다."));

        // 리뷰 등록할 매장 찾음
        // 삭제할 리뷰의 별점으로 매장의 별점 수정
        cancelStore(review);
        reviewRepository.delete(review);
        return String.format("%d 리뷰 삭제", review.getId());
    }

    /**
     * 커스터머가 등록한 모든 리뷰
     *
     * @param userId
     * @param pageable
     * @return : 등록한 리뷰 리스트
     */
    public Page<ReviewDto> searchReview(Long userId, Pageable pageable) {
        return reviewRepository.findByCustomerId(userId, pageable)
                .map(ReviewDto::from);
    }

    /**
     * 파트너의 특정 매장에 등록된 모든 리뷰
     *
     * @param userId
     * @param pageable
     * @return : 등록된 리뷰 리스트
     */
    public Page<ReviewDto> searchReviewByStore(Long userId, Long storeId, Pageable pageable) {
        return reviewRepository.findByPartnerIdAndStoreId(userId, storeId, pageable)
                .map(ReviewDto::from);
    }

}
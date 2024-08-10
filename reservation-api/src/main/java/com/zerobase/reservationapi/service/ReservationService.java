package com.zerobase.reservationapi.service;


import com.zerobase.reservationapi.client.StoreClient;
import com.zerobase.reservationapi.client.from.CheckDateForm;
import com.zerobase.reservationapi.client.from.DecreaseForm;
import com.zerobase.reservationapi.client.from.IncreaseForm;
import com.zerobase.reservationapi.domain.reservation.dto.ReservationDto;
import com.zerobase.reservationapi.domain.reservation.entity.Reservation;
import com.zerobase.reservationapi.domain.reservation.form.ConfirmReservation;
import com.zerobase.reservationapi.domain.reservation.form.MakeReservation;
import com.zerobase.reservationapi.domain.store.StoreDto;
import com.zerobase.reservationapi.domain.store.StoreReservationInfoDto;
import com.zerobase.reservationapi.exception.ReservationException;
import com.zerobase.reservationapi.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import static com.zerobase.reservationapi.domain.reservation.type.Status.*;
import static com.zerobase.reservationapi.exception.ErrorCode.*;


@Service
@Slf4j
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StoreClient storeClient;

    /**
     * 매장 예약
     *
     * @param id
     * @param form : reservationInfoId, headCount(예약 인원), phone, reservationDate
     * @return 저장된 예약 정보
     */
    public ReservationDto makeReservation(Long id, MakeReservation form) {

        // 예약 상세 정보 가져옴
        StoreReservationInfoDto storeReservationInfoDto = storeClient.getStoreReservationInfo(form.getReservationInfoId());

        // 매장 정보 가져옴
        StoreDto storeDto = storeClient.getStore(storeReservationInfoDto.getStoreId());


        checkReservationDate(form, storeDto.getId());
        checkStoreIsDeleted(storeDto);
        checkMaked(id, storeReservationInfoDto, form.getReservationDate());
        checkCount(form.getHeadCount(), storeReservationInfoDto.getMinCount(), storeReservationInfoDto.getMaxCount());
        checkReservationIsClosed(storeReservationInfoDto.getClosed(), form.getReservationDate());

        Reservation reservation = reservationRepository.save(Reservation.of(id, form, storeDto.getId(), storeDto.getPartnerId(), storeReservationInfoDto.getId()));

        return ReservationDto.from(reservation);
    }

    /**
     * 예약 신청한 날짜가 예약 오픈된 날짜인지 확인
     *
     * @param form
     */
    private void checkReservationDate(MakeReservation form, Long storeId) {
        // 예약이 오픈되지 않은 날짜에 신청한 경우 예외 발생 : CANNOT_RESERVATION_DATE "예약 가능한 날짜가 아닙니다."
        CheckDateForm request = CheckDateForm.builder()
                .reservationDate(form.getReservationDate())
                .storeId(storeId).build();
        if (!storeClient.checkReservationDate(request)) {
            throw new ReservationException(CANNOT_RESERVATION_DATE);
        }
    }

    /**
     * 예약마감되었는데 예약 신청했는지 확인
     *
     * @param closed
     * @param reservationDate
     */
    private void checkReservationIsClosed(Map<LocalDate, Integer> closed, LocalDate reservationDate) {
        // 마감된 정보에(closed의 value가 -1일때) 신청한 경우 예외 발생 : RESERVATION_CLOSED "예약이 마감되었습니다."
        if (closed.get(reservationDate) == -1)
            throw new ReservationException(RESERVATION_CLOSED);
    }

    /**
     * 삭제된 매장에 예약 신청했는지 확인
     */
    private void checkStoreIsDeleted(StoreDto storeDto) {
        // 삭제된 매장에 deleted==true 신청한 경우 예외 발생 : ALREADY_DELETED_STORE "이미 삭제된 매장입니다."
        if (storeDto.isDeleted()) {
            throw new ReservationException(ALREADY_DELETED_STORE);
        }
    }

    /**
     * 이미 신청한 예약정보에 중복신청 하는지 확인
     *
     * @param id
     * @param storeReservationInfo
     * @param reservationDate
     */
    private void checkMaked(Long id, StoreReservationInfoDto storeReservationInfo, LocalDate reservationDate) {
        Optional<Reservation> reservation = reservationRepository.findByCustomerIdAndStoreReservationInfoIdAndReservationDate(id, storeReservationInfo.getId(), reservationDate);

        // 예약 정보에 같은 날짜로 중복 신청하는 경우 예외 발생 : ALREADY_MAKE_RESERVATION "예약정보가 존재합니다."
        if (reservation.isPresent() && reservationDate.isEqual(reservation.get().getReservationDate())) {
            throw new ReservationException(ALREADY_MAKE_RESERVATION);
        }
    }

    /**
     * 예약 최소인원보다 적게 신청하거나 최대인원보다 많게 신청했는지 확인
     *
     * @param headCount
     * @param minCount
     * @param maxCount
     */
    private void checkCount(int headCount, int minCount, int maxCount) {
        if (headCount < minCount) {
            // 예약 인원이 최소인원보다 적은 경우 예외 발생 : LOWER_STORE_MIN_CAPACITY "예약 가능 인원이 부족합니다."
            throw new ReservationException(LOWER_STORE_MIN_CAPACITY);
        } else if (headCount > maxCount) {
            // 예약 인원이 최대인원보다 많은 경우 예외 발생 : OVER_STORE_MAX_CAPACITY "예약 가능인원을 초과하였습니다."
            throw new ReservationException(OVER_STORE_MAX_CAPACITY);
        }
    }

    /**
     * 신청된 예약 승인, 거절
     *
     * @param partnerId
     * @param form      : reservationId, status(예약/승인)
     * @return 수정한 예약 정보
     */
    @Transactional
    public ReservationDto changeReservationStatus(Long partnerId, ConfirmReservation form) {

        Reservation reservation = reservationRepository.findById(form.getReservationId())
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        // 신청한 매장 예약 상세정보 가져옴
        StoreReservationInfoDto storeReservationInfoDto = storeClient.getStoreReservationInfo(reservation.getStoreReservationInfoId());

        // 본인의 매장에 신청한 예약정보를 수정하려고 하는 건지 확인
        StoreDto storeDto = storeClient.getStoreWithPartner(storeReservationInfoDto.getStoreId(), partnerId);

        checkReservationStatus(reservation);
        if (APPROVED.equals(form.getStatus())) {
            checkCanReservationCount(reservation.getHeadCount(), storeReservationInfoDto.getCount());
            // 해당날짜의 신청한 예약인원만큼 잔여 인원 감소
            DecreaseForm request = DecreaseForm.builder()
                    .storeReservationInfoId(storeReservationInfoDto.getId())
                    .reservationDate(reservation.getReservationDate())
                    .count(reservation.getHeadCount())
                    .build();
            storeClient.decreaseCount(request);
        }

        reservation.changeStatus(form.getStatus());

        return ReservationDto.from(reservation);
    }

    /**
     * 이미 예약 상태를 변경했는지 확인
     *
     * @param reservation
     */
    private void checkReservationStatus(Reservation reservation) {
        if (APPROVED.equals(reservation.getStatus()))
            // 이미 승인한 예약인 경우 예외 발생 : ALREADY_CHANGE_STATUS "이미 승인된 예약입니다."
            throw new ReservationException(ALREADY_CHANGE_STATUS, "이미 승인된 예약입니다.");
        else if (REJECTED.equals(reservation.getStatus())) {
            // 이미 거절한 예약인 경우 예외 발생 : ALREADY_CHANGE_STATUS "이미 거절된 예약입니다."
            throw new ReservationException(ALREADY_CHANGE_STATUS, "이미 거절된 예약입니다.");
        }
    }

    /**
     * 예약 가능한 잔여 인원을 신청인원이 초과하는지 확인
     *
     * @param headCount
     * @param count
     */
    private void checkCanReservationCount(int headCount, int count) {
        if (headCount > count) {
            // 신청인원이 예약 가능 잔여인원보다 많은 경우 예외 발생 : OVER_RESERVATION_COUNT "예약 가능 인원을 초과합니다."
            throw new ReservationException(OVER_RESERVATION_COUNT);
        }
    }

    /**
     * 고객이 자신이 예약한 리스트 확인
     *
     * @param memberId
     * @param pageable
     * @return 예약 리스트
     */
    public Page<ReservationDto> searchReservationByMember(Long memberId, Pageable pageable) {

        return reservationRepository.findByCustomerId(memberId, pageable)
                .map(ReservationDto::from);
    }

    /**
     * 고객이 특정 매장에 예약한 리스트 확인
     *
     * @param memberId
     * @param storeId
     * @param pageable
     * @return 예약 리스트
     */
    public Page<ReservationDto> searchReservationByMemberWithStore(Long memberId, Long storeId, Pageable pageable) {
        return reservationRepository.findByCustomerIdAndStoreId(memberId, storeId, pageable)
                .map(ReservationDto::from);
    }

    /**
     * 파트너가 자신의 특정 매장 예약 리스트 확인
     *
     * @param partnerId
     * @param storeId
     * @param date
     * @param pageable
     * @return 예약 리스트
     */
    public Page<ReservationDto> searchReservationByPartner(Long partnerId, Long storeId, LocalDate date, Pageable pageable) {
        // 자신의 매장의 예약리스트를 보는지 확인하기위해
        // 바로 store id를 이용하지 않고
        // partnerId와 매장명을 이용해 받아온 store의 id를 이용
        // 예외가 발생하면 요청한 유저의 매장이 아닌것
        StoreDto storeDto = storeClient.getStoreWithPartner(storeId, partnerId);
        return reservationRepository.findByStoreIdAndReservationDate(storeDto.getId(), date, pageable)
                .map(ReservationDto::from);
    }

    /**
     * 매장 예약 취소
     *
     * @param customerId
     * @param id
     * @return 취소한 예약 정보
     */
    @Transactional
    public ReservationDto cancelReservation(Long customerId, Long id) {
        // 취소하려는 예약이 본인이 신청한 예약인지 확인
        Reservation reservation = reservationRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ReservationException(UNMATCHED_MEMBER_RESERVATION));

        // 신청한 매장 예약 상세정보 가져옴
        StoreReservationInfoDto storeReservationInfoDto = storeClient.getStoreReservationInfo(reservation.getStoreReservationInfoId());

        // 예약가능 잔여인원을 취소한 인원만큼 증가
        if (APPROVED.equals(reservation.getStatus())) {
            IncreaseForm request = IncreaseForm.builder()
                    .storeReservationInfoId(storeReservationInfoDto.getId())
                    .reservationDate(reservation.getReservationDate())
                    .count(reservation.getHeadCount())
                    .build();
            storeClient.increaseCount(request);
        }

        reservationRepository.delete(reservation);
        return ReservationDto.from(reservation);
    }

    /**
     * 매장 방문 확인
     *
     * @param customerId
     * @param id
     * @return : 수정한 예약 정보
     */
    @Transactional
    public ReservationDto visitReservation(Long customerId, Long id) {
        // 취소하려는 예약이 본인이 신청한 예약인지 확인
        Reservation reservation = reservationRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        if (REJECTED.equals(reservation.getStatus())) {
            // 예약이 거절당한 경우 예외 발생 : CHECK_RESERVATION_STATUS "예약이 거절되었습니다."
            throw new ReservationException(CHECK_RESERVATION_STATUS, "예약이 거절되었습니다.");
        } else if (PENDING.equals(reservation.getStatus())) {
            // 예약이 확정되지 않은 경우  예외 발생 : CHECK_RESERVATION_STATUS "예약이 확인중입니다."
            throw new ReservationException(CHECK_RESERVATION_STATUS, "예약이 확인중입니다.");
        }

        checkReservationDate(reservation);
        checkCanInStore(reservation);

        reservation.changeVisited(true);

        return ReservationDto.from(reservation);
    }

    /**
     * 예약한 시간 10분전 보다 방문 확인하는지 확인
     * 예약시간을 지나 방문 확인하는지 확인
     *
     * @param reservation
     */
    private void checkCanInStore(Reservation reservation) {
        StoreReservationInfoDto storeReservationInfoDto = storeClient.getStoreReservationInfo(reservation.getStoreReservationInfoId());

        LocalTime canCheckTime = storeReservationInfoDto.getStartAt();

        // 예약시간 10분전보다 일찍 방문확인 하는 경우 예외 발생 : CANNOT_CHECK_YET "[예약 시간 : %s , 현재 시간 : %s] 방문 확인은 10분전부터 가능합니다.", canCheckTime, LocalTime.now()
        if (LocalTime.now().isBefore(canCheckTime.minusMinutes(10))) {
            String errorDescription = String.format("[예약 시간 : %s , 현재 시간 : %s] 방문 확인은 10분전부터 가능합니다.", canCheckTime, LocalTime.now());
            throw new ReservationException(CANNOT_CHECK_YET, errorDescription);
        }

        // 예약 시간을 지나 방문 확인하는 경우 예외 발생 : OVER_RESERVATION_TIME "[예약 시간 : %s , 현재 시간 : %s] 예약 시간이 지났습니다.", canCheckTime, LocalTime.now()
        if (LocalTime.now().isAfter(canCheckTime)) {
            String errorDescription = String.format("[예약 시간 : %s , 현재 시간 : %s] 예약 시간이 지났습니다.", canCheckTime, LocalTime.now());
            throw new ReservationException(OVER_RESERVATION_TIME, errorDescription);
        }
    }

    /**
     * 예약한날 방문 확인하는지 확인
     *
     * @param reservation
     */
    private void checkReservationDate(Reservation reservation) {
        // 예약한 날이 아닌 다른날 방문 확인 하는 경우 예외 발생 "[예약 날짜 : %s] 를 확인해주세요.", reservation.getReservationDate()
        if (!LocalDate.now().isEqual(reservation.getReservationDate())) {
            String errorDescription = String.format("[예약 날짜 : %s] 를 확인해주세요.", reservation.getReservationDate());
            throw new ReservationException(NOT_TODAY_RESERVATION, errorDescription);
        }
    }
}
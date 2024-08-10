package com.zerobase.reservationapi.domain.reservation.entity;


import com.zerobase.reservationapi.domain.BaseEntity;
import com.zerobase.reservationapi.domain.reservation.form.MakeReservation;
import com.zerobase.reservationapi.domain.reservation.type.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@AuditOverride(forClass = BaseEntity.class)
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId; // 예약 신청한 고객 id
    private Long partnerId; // 예약 신청한 고객 id
    private Long storeId; // 예약 받는 매장 id

    private String phone; // 예약 신청한 고객이 연락받을 번호


    private Long storeReservationInfoId; // 예약 신청한 매장의 예약상세정보

    private LocalDate reservationDate; // 예약 신청 날짜
    private int headCount; // 예약 신청 인원
    @Enumerated(EnumType.STRING)
    private Status status; // 예약 상태 PENDING:대기 APPROVED:승인 REJECTED:거절 STORE_DELETED:매장삭제됨
    private boolean visit; // 방문 여부

    public static Reservation of(Long id, MakeReservation form, Long storeId, Long partnerId, Long storeReservationInfoId) {
        return Reservation.builder()
                .customerId(id)
                .partnerId(partnerId)
                .storeId(storeId)
                .phone(form.getPhone())
                .reservationDate(form.getReservationDate())
                .storeReservationInfoId(storeReservationInfoId)
                .headCount(form.getHeadCount())
                .status(Status.PENDING)
                .visit(false)
                .build();
    }

    // 매장 파트너가 예약을 승인/거절하는 경우 상태 업데이트
    public void changeStatus(Status status) {
        this.status = status;
    }

    // 매장 방문확인한 경우 방문 상태 업데이트
    public void changeVisited(boolean visit) {
        this.visit = visit;
    }
}
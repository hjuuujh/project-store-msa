package com.zerobase.reservationapi.domain.reservation.dto;


import com.zerobase.reservationapi.domain.reservation.entity.Reservation;
import com.zerobase.reservationapi.domain.reservation.type.Status;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private Long id;
    private Long memberId;
    private Long partnerId;
    private Long storeId;
    private String phone;
    private String storeName;
    private Long reservationInfoId;
    private LocalDate reservationDate;
    private int headCount;
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean visit;

    public static ReservationDto from(Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getId())
                .memberId(reservation.getCustomerId())
                .partnerId(reservation.getPartnerId())
                .storeId(reservation.getStoreId())
                .reservationDate(reservation.getReservationDate())
                .reservationInfoId(reservation.getStoreReservationInfoId())
                .headCount(reservation.getHeadCount())
                .status(reservation.getStatus())
                .visit(reservation.isVisit())
                .build();
    }


}
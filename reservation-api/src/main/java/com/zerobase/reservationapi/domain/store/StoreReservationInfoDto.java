package com.zerobase.reservationapi.domain.store;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreReservationInfoDto {
    private Long id;

    private Long partnerId;
    private Long storeId;
    private LocalTime startAt;
    private LocalTime endAt;
    private int minCount;
    private int maxCount;
    private int count;
    private Map<LocalDate, Integer> closed;


}
package com.zerobase.storeapi.client.from;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncreaseForm {
    private Long storeReservationInfoId;
    private LocalDate reservationDate;
    private Integer count;
}

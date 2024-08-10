package com.zerobase.reservationapi.domain.store;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    private Long id;
    private Long partnerId;
    private String name;
    private String description;
    private String address;
    private LocalTime openAt;
    private LocalTime closeAt;
    private List<LocalDate> dates;
    private float lon;
    private float lat;
    private double rating;
    private boolean deleted;

}
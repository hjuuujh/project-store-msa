package com.zerobase.reservationapi.client;

import com.zerobase.reservationapi.client.from.ChangeRatingForm;
import com.zerobase.reservationapi.client.from.CheckDateForm;
import com.zerobase.reservationapi.client.from.DecreaseForm;
import com.zerobase.reservationapi.client.from.IncreaseForm;
import com.zerobase.reservationapi.domain.store.StoreDto;
import com.zerobase.reservationapi.domain.store.StoreReservationInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "store", url = "${external-api.store.url}")
public interface StoreClient {

    // 매장 예약 상세정보 가져옴
    @GetMapping("/info/{id}")
    StoreReservationInfoDto getStoreReservationInfo(@PathVariable(value = "id") Long id);

    // 매장 정보 가져옴
    @GetMapping("/{id}")
    StoreDto getStore(@PathVariable(value = "id") Long id);

    // 요청한 유저가 매장의 파트너가 맞는지 확인하고 매장정보 가져옴
    @GetMapping("/{id}/{partnerId}")
    StoreDto getStoreWithPartner(@PathVariable(value = "id") Long id, @PathVariable(value = "partnerId") Long partnerId);

    // 예약 가능한 날짜인지 확인
    @PostMapping("/date/check")
    boolean checkReservationDate(@RequestBody CheckDateForm checkDateForm);

    // 해당 날짜의 예약 가능 잔여인원 감소
    @PostMapping("/decrease")
    void decreaseCount(@RequestBody DecreaseForm decreaseForm);

    // 해당 날짜의 예약 가능 잔여인원 증가
    @PostMapping("/increse")
    void increaseCount(@RequestBody IncreaseForm increaseForm);

    // 후기 추가 or 수정시 매장의 별점 변경
    @PostMapping("/rating")
    void changeRating(@RequestBody ChangeRatingForm changeRatingForm);

    // 후기 삭제시 매장의 별점 감소
    @PostMapping("/rating/decrease")
    void decreaseRating(@RequestBody ChangeRatingForm changeRatingForm);
}

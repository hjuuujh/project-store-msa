package com.zerobase.storeapi.controller.partner;


import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.client.from.ChangeRatingForm;
import com.zerobase.storeapi.client.from.CheckDateForm;
import com.zerobase.storeapi.client.from.DecreaseForm;
import com.zerobase.storeapi.client.from.IncreaseForm;
import com.zerobase.storeapi.controller.ResponseError;
import com.zerobase.storeapi.controller.ValidationErrorResponse;
import com.zerobase.storeapi.domain.store.form.*;
import com.zerobase.storeapi.service.StoreSearchService;
import com.zerobase.storeapi.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;
    private final StoreSearchService storeSearchService;
    private final ValidationErrorResponse validationErrorResponse;
    private final MemberClient memberClient;

    /**
     * 매장 정보 등록
     *
     * @param token
     * @param form   : name, description, address, openAt, closedAt
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 저장한 매장 정보
     * @throws IOException
     * @throws ParseException
     */
    @PostMapping
    public ResponseEntity<?> registerStore(@RequestHeader(name = "Authorization") String token,
                                           @RequestBody @Valid RegisterStore form, Errors errors) throws IOException, ParseException {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }
        System.out.println(token);
        return ResponseEntity.ok(storeService.registerStore(memberClient.getMemberId(token), form));
    }

    /**
     * 매장 예약 상세정보 등록
     *
     * @param token
     * @param form   : storeId, startAt(예약 가능 시작시간), endAt(예약 가능 마감시간), minCount(예약 최소인원), maxCount(예약 최대인원)
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 저장한 매장 예약 정보
     */
    @PostMapping("/reservation/info")
    public ResponseEntity<?> addStoreReservationInfo(@RequestHeader(name = "Authorization") String token,
                                                     @RequestBody @Valid List<RegisterStoreReservationInfo> form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(storeService.addStoreReservationInfo(memberClient.getMemberId(token), form));
    }

    /**
     * 매장 정보 수정
     *
     * @param token
     * @param form   : id, name, description, address, openAt, closedAt, infos(예약 상세 정보), 예약 가능 날짜
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 수정한 매장 정보
     * @throws IOException
     * @throws ParseException
     */
    @PutMapping
    public ResponseEntity<?> updateStore(@RequestHeader(name = "Authorization") String token,
                                         @RequestBody @Valid UpdateStore form, Errors errors) throws IOException, ParseException {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(storeService.updateStore(memberClient.getMemberId(token), form));
    }

    /**
     * 매장 예약 상세정보 수정
     *
     * @param token
     * @param form   : id, storeId, startAt(매장 오픈 시간), endAt(매장 마감 시간), minCount(예약 최소인원), maxCount(예약 최대인원), exist(기존에 있던 정보인지)
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 수정한 매장 예약 정보
     */
    @PatchMapping("/reservation/info")
    public ResponseEntity<?> updateStoreReservationInfo(@RequestHeader(name = "Authorization") String token,
                                                        @RequestBody @Valid List<UpdateReservationInfo> form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(storeService.updateStoreReservationInfo(memberClient.getMemberId(token), form));
    }

    /**
     * 예약 가능 날짜 수정
     *
     * @param token
     * @param form   : id, dates(예약 가능 날짜들)
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return : 수정한 매장 정보
     */
    @PatchMapping("/reservation/date")
    public ResponseEntity<?> updateStoreReservationDate(@RequestHeader(name = "Authorization") String token,
                                                        @RequestBody @Valid UpdateStoreDate form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(storeService.updateStoreReservationDate(memberClient.getMemberId(token), form));
    }

    /**
     * 예약 마감 정보 수정
     *
     * @param token
     * @param form  : id, date(해당 날짜), closed(-1: 예약 마감, int: 예약가능 인원)
     * @return : 수정한 매장 예약 정보
     */
    @PatchMapping("/reservation/date/closed")
    public ResponseEntity<?> updateStoreReservationClosed(@RequestHeader(name = "Authorization") String token,
                                                          @RequestBody UpdateReservationClosed form) {

        return ResponseEntity.ok(storeService.updateStoreReservationClosed(memberClient.getMemberId(token), form));
    }

    /**
     * 매장 정보 삭제
     *
     * @param token
     * @param id
     * @return 삭제된 매장 정보
     */
    @PatchMapping
    public ResponseEntity<?> deleteStore(@RequestHeader(name = "Authorization") String token
            , @RequestParam Long id) {

        return ResponseEntity.ok(storeService.deleteStore(memberClient.getMemberId(token), id));
    }

    /**
     * 매장 예약 정보 삭제
     *
     * @param token
     * @param form  : ids(삭제할 매장 예약 상세정보 id 리스트), storeId
     * @return : 수정한 매장 정보
     */
    @DeleteMapping("/reservation/info")
    public ResponseEntity<?> deleteStoreReservationInfo(@RequestHeader(name = "Authorization") String token
            , @RequestBody DeleteReservationInfo form) {

        return ResponseEntity.ok(storeService.deleteStoreReservationInfo(memberClient.getMemberId(token), form));
    }

    /**
     * 파트너 유저가 등록한 매장들 리턴
     *
     * @param token
     * @param pageable
     * @return : 매장정보 리스트
     */
    @GetMapping("/partner")
    public ResponseEntity<?> searchStoreByPartner(@RequestHeader(name = "Authorization") String token, final Pageable pageable) {
        return ResponseEntity.ok(storeSearchService.searchStoreByPartner(memberClient.getMemberId(token), pageable));
    }

    /**
     * 매장 예약 상세정보 리턴
     *
     * @param id
     * @return 매장예약상세 정보
     */
    @GetMapping("/info/{id}")
    public ResponseEntity<?> getStoreReservationInfo(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreReservationInfo(id));
    }

    /**
     * 매장 정보 리턴
     *
     * @param id
     * @return 매장 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStore(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStore(id));
    }

    /**
     * 요청한 유저가 매장의 파트너가 맞는지 확인하고 매장정보 리턴
     *
     * @param id
     * @param partnerId
     * @return 매장 정보
     */
    @GetMapping("/{id}/{partnerId}")
    public ResponseEntity<?> getStoreWithPartner(@PathVariable Long id, @PathVariable Long partnerId) {
        return ResponseEntity.ok(storeService.getStoreWithPartner(id, partnerId));
    }

    /**
     * 예약 가능한 날짜인지 확인
     *
     * @param form : reservationDate(예약 원하는 날짜), storeId
     * @return boolean
     */
    @PostMapping("/date/check")
    public ResponseEntity<?> checkReservationDate(@RequestBody CheckDateForm form) {
        return ResponseEntity.ok(storeService.checkReservationDate(form));
    }

    /**
     * 해당 날짜의 예약 가능 잔여인원 감소
     *
     * @param form : storeReservationInfoId, reservationDate(예약 원하는 날짜), count
     * @return ok
     */
    @PostMapping("/decrease")
    public ResponseEntity<?> decreaseCount(@RequestBody DecreaseForm form) {
        storeService.decreaseCount(form);
        return ResponseEntity.ok().build();
    }

    /**
     * 해당 날짜의 예약 가능 잔여인원 증가
     *
     * @param form : storeReservationInfoId, reservationDate(예약 원하는 날짜), count
     * @return ok
     */
    @PostMapping("/increase")
    public ResponseEntity<?> increaseCount(@RequestBody IncreaseForm form) {
        storeService.increaseCount(form);
        return ResponseEntity.ok().build();
    }

    /**
     * 후기 추가 or 수정시 매장의 별점 변경
     *
     * @param form : storeId, rating
     * @return ok
     */
    @PostMapping("/rating")
    public ResponseEntity<?> changeRating(@RequestBody ChangeRatingForm form) {
        storeService.changeRating(form);
        return ResponseEntity.ok().build();
    }

    /**
     * 후기 삭제시 매장의 별점 감소
     *
     * @param form : storeId, rating
     * @return ok
     */
    @PostMapping("/rating/decrease")
    public ResponseEntity<?> decreaseRating(@RequestBody ChangeRatingForm form) {
        storeService.decreaseRating(form);
        return ResponseEntity.ok().build();
    }

}
package com.zerobase.storeapi.controller.search;


import com.zerobase.storeapi.service.StoreSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/search")
public class StoreSearchController {
    private final StoreSearchService storeSearchService;

    /**
     * 키워드가 포함된 매장명을 가진 매장들 리턴
     *
     * @param keyword
     * @param pageable
     * @return : 매장정보 리스트
     */
    @GetMapping
    public ResponseEntity<?> searchStoreByName(@RequestParam String keyword, final Pageable pageable) {
        return ResponseEntity.ok(storeSearchService.searchStoreByName(keyword, pageable));
    }

    /**
     * 가나다 순으로 매장들 리턴
     *
     * @param pageable
     * @return : 매장정보 리스트
     */
    @GetMapping("/alphabet")
    public ResponseEntity<?> searchStoreAlphabeticalOrder(final Pageable pageable) {
        return ResponseEntity.ok(storeSearchService.searchStoreAlphabeticalOrder(pageable));
    }

    /**
     * 별점순 매장들 리턴
     *
     * @param pageable
     * @return : 매장정보 리스트
     */
    @GetMapping("/rating")
    public ResponseEntity<?> searchStoreRatingOrder(final Pageable pageable) {
        return ResponseEntity.ok(storeSearchService.searchStoreRatingOrder(pageable));
    }

    /**
     * 요청한 주소와 가까운 순으로 매장들 리턴
     *
     * @param address
     * @param pageable
     * @return : 매장정보 리스트
     * @throws IOException
     * @throws ParseException
     */
    @GetMapping("/distance")
    public ResponseEntity<?> searchStoreDistanceOrder(@RequestParam String address, final Pageable pageable) throws IOException, ParseException {
        return ResponseEntity.ok(storeSearchService.searchStoreDistanceOrder(address, pageable));
    }

    /**
     * 특정 매장의 상세정보 리턴
     *
     * @param id
     * @return : 매장 정보
     */
    @GetMapping("/detail")
    public ResponseEntity<?> detailStore(@RequestParam Long id) {
        return ResponseEntity.ok(storeSearchService.detailStore(id));
    }

}
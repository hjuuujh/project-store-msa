package com.zerobase.storeapi.client;

import com.zerobase.storeapi.client.from.TokenForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "member", url = "${external-api.member.url}")
public interface MemberClient {

    // 요청한 유저의 id 가져옴
    @GetMapping("/id")
    Long getMemberId(@RequestHeader(name = "Authorization") String token);
}

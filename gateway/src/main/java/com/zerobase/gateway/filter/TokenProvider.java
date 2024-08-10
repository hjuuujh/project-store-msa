package com.zerobase.gateway.filter;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
@RefreshScope // 특정행동 수행시 변경된 설정파일의 설정이 앱의 재배포 과정없이 실시간 반영
public class TokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    String resolveTokenRole(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("roles").toString();
        } catch (Exception e) {
            log.info("유저 권한 체크 실패");
            return "e";
        }
    }

    // 토큰 만료되었는지 확인 (1일이 지났는지)
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false;
        log.info("token: {}", token);

        try {

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            log.info("claims: {}", claimsJws.getBody());
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException e) {
            throw new SecurityException(e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

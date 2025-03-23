package com.bsslab.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 관련 유틸리티 클래스
 */
@Component
@Slf4j
public class JwtUtils {

    private final JwtProperties jwtProperties;
    private final Key key;

    @Autowired
    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // 안전한 키 생성 - HS512에 충분한 키 크기 보장
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * 인증 정보를 기반으로 JWT 토큰 생성
     *
     * @param authentication 인증 정보
     * @return 생성된 JWT 토큰
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 이름 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param authToken 검증할 토큰
     * @return 유효성 여부
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            log.error("유효하지 않은 JWT 서명: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("유효하지 않은 JWT 토큰: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims 문자열이 비어있음: {}", e.getMessage());
        }

        return false;
    }
}
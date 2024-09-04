package com.project.react_tft.util;

import io.jsonwebtoken.JwtException;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTUtil {
    @Value("${jwt.secret}")
    private String key;

    public String generateToken(Map<String, Object> valueMap, int days) {
        log.info("generate token key" + key);

        //헤더
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        //페이로드 부분
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);

        //유효시간.  토큰 생성시간.
        int time = (60 * 24) * days; //시간설정. 60 * 24 는 하루

        String jwtStr = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))    // 분 단위로 처리 나중에 plusDays()로 변경 해줘야 함
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();


        return jwtStr;
    }

    public Map<String, Object> validateToken(String token)throws JwtException {
        Map<String, Object> claims = null;

         claims = Jwts.parser()
                .setSigningKey(key.getBytes()).build()  // 서명 검증을 위한 키 설정
                .parseSignedClaims(token)               // 토큰 파싱 및 클레임 추출
                .getBody();

        return claims;
    }
}

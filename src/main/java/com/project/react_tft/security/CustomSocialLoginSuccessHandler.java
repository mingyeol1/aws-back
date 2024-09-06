package com.project.react_tft.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.react_tft.dto.MemberSecurityDTO;
import com.project.react_tft.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("-----------------------------------------");
        log.info("CustomLoginSuccessHandler onAuthenticationSuccess");
        log.info(authentication.getPrincipal());

        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();

        String encodePw = memberSecurityDTO.getMpw();

        // 소셜 로그인이고 회원 패스워드가 '1111' (기본값) 일 경우 비밀번호 변경 유도
        if (memberSecurityDTO.isSocial() && ("1111".equals(encodePw) || passwordEncoder.matches("1111", encodePw))) {
            log.info("비번 바꾸던가");
        }

        // JWT 토큰 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("mid", memberSecurityDTO.getMid());
        claims.put("memail", memberSecurityDTO.getMemail());

        String accessToken = jwtUtil.generateToken(claims, 1);
        String refreshToken = jwtUtil.generateToken(claims, 30);

        // 쿠키 생성
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(60 * 60); // 1 hour
        accessTokenCookie.setAttribute("SameSite", "None");
        accessTokenCookie.setDomain("tft.p-e.kr"); // 도메인 설정

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
        refreshTokenCookie.setAttribute("SameSite", "None");
        refreshTokenCookie.setDomain("tft.p-e.kr"); // 도메인 설정

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // CORS 헤더 설정 (필요한 경우)
        response.setHeader("Access-Control-Allow-Origin", "https://www.tft.p-e.kr");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 클라이언트로 리다이렉트
        response.sendRedirect("https://www.tft.p-e.kr");

    }
}

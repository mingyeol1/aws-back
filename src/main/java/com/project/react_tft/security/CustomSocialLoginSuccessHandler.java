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

        String accessToken = jwtUtil.generateToken(claims, 1);  // 1시간 유효
        String refreshToken = jwtUtil.generateToken(claims, 30);  // 30일 유효

        // HTTPS 환경 여부 확인 (배포 환경)
        boolean isSecure = request.isSecure(); // 배포 환경은 HTTPS이므로 true여야 함

        // 쿠키를 수동으로 헤더에 추가하면서 SameSite 설정
        String accessTokenCookie = String.format(
                "accessToken=%s; Path=/; Max-Age=%d; SameSite=None; %s",
                accessToken,
                60 * 60, // 1 hour
                isSecure ? "Secure; HttpOnly" : "HttpOnly" // HTTPS일 경우 Secure 추가
        );

        String refreshTokenCookie = String.format(
                "refreshToken=%s; Path=/; Max-Age=%d; SameSite=None; %s",
                refreshToken,
                60 * 60 * 24 * 30, // 30 days
                isSecure ? "Secure; HttpOnly" : "HttpOnly"
        );

        // 헤더에 쿠키 추가
        response.addHeader("Set-Cookie", accessTokenCookie);
        response.addHeader("Set-Cookie", refreshTokenCookie);

        // 배포 환경 리다이렉트
        String redirectUrl = "https://www.tft.p-e.kr";  // 배포된 URL

        /*
         * 로컬에서 실행할 경우:
         * 1. isSecure를 false로 강제 설정 (로컬에서는 HTTPS 사용 불가)
         * 2. redirectUrl을 로컬 URL로 변경
         *
         *
         * boolean isSecure = false; // 로컬에서는 HTTPS가 아니므로 false로 설정
         * String redirectUrl = "http://localhost:3000/";  // 로컬 환경의 React 앱 URL
         */

        // 클라이언트로 리다이렉트 (배포 환경)
        response.sendRedirect(redirectUrl);
    }
}
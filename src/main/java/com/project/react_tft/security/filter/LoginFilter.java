package com.project.react_tft.security.filter;

import com.google.gson.Gson;
import com.project.react_tft.domain.Member;
import com.project.react_tft.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private final MemberService memberService;

    public LoginFilter(String defaultFilterProcessesUrl, MemberService memberService) {
        super(defaultFilterProcessesUrl);
        this.memberService = memberService;

        // 기본 실패 핸들러 설정 추가
        setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                log.error("인증 실패: " + exception.getMessage());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");

                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("status", "error");
                errorDetails.put("message", "아이디 또는 비밀번호가 일치하지 않습니다");

                response.getWriter().write(new Gson().toJson(errorDetails));
            }
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 필터 실행 중");

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("인증 방식이 지원되지 않습니다: " + request.getMethod());
        }

        Map<String, String> jsonData = parseRequestJSON(request);
        if (jsonData == null) {
            throw new AuthenticationServiceException("요청 데이터 파싱 실패");
        }

        String mid = jsonData.get("mid");
        String mpw = jsonData.get("mpw");

        if (mid == null || mpw == null) {
            throw new AuthenticationServiceException("아이디 또는 비밀번호가 제공되지 않았습니다");
        }

        log.info("로그인 시도 ID: " + mid);

        // memberService를 통해 사용자 검증
        try {
            Member member = memberService.login(mid, mpw);

            // member가 null이면 로그인 실패
            if (member == null) {
                throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않거나 삭제된 계정입니다");
            }

            // 인증 객체 생성 및 반환
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(mid, mpw);



            // 인증 매니저에게 인증 위임
            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: " + e.getMessage());
            throw new AuthenticationServiceException("인증 처리 중 오류: " + e.getMessage());
        }
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error("JSON 파싱 오류: " + e.getMessage());
        }
        return null;
    }
}
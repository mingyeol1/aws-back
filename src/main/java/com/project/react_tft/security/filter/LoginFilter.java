package com.project.react_tft.security.filter;


import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Log4j2
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    public LoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("로그인필터로그인필터로그인필터로그인필터로그인필터로그인필터로그인필터로그인필터로그인필터로그인필터");

        //요청이 GET 메서드인 경우 인증을 처리하지 않고 null값 반환
        if (request.getMethod().equalsIgnoreCase("GET")) {   // .getMethod로 GET 방식은 처리하지 않음
            log.info("GET METHOD NOT SUPPORT");
            return null;
        }

        Map<String, String> jsonData = parseRequestJSON(request);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                (jsonData.get("mid"), jsonData.get("mpw"));

        log.info(jsonData + "jsonDatajsonDatajsonDatajsonDatajsonData");




        return getAuthenticationManager().authenticate(authenticationToken);
    }


    // 요청의 JSON 데이터를 파싱하여 Map<String, String> 형태로 반환
    private Map<String, String> parseRequestJSON(HttpServletRequest request) {

        //JSON 데이터를 분석해서 mid, mpw전달값을 Map으로 처리
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            // GSON으로 JSON 데이터를 Map으로 변환
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}

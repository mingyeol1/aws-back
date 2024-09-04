package com.project.react_tft.security.filter.handler;

import com.google.gson.Gson;
import com.project.react_tft.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {
   private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("로그인에 성공했는데요? 로그인 성공 핸들러인데요?");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info(authentication);
        log.info(authentication.getName());

        Map<String, Object> claim = Map.of("mid", authentication.getName());
        //Access Token 유효기간
        String accessToken = jwtUtil.generateToken(claim,1); //1일
        //Refresh Token 유효기간
        String refreshToken = jwtUtil.generateToken(claim,30); //30일

        Gson gson= new Gson();

        Map<String,String> keyMap = Map.of(
                "accessToken", accessToken,
                "refreshToken",refreshToken);

        String jsonStr = gson.toJson(keyMap);
        response.getWriter().print(jsonStr);

    }
}

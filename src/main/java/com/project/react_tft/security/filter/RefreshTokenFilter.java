package com.project.react_tft.security.filter;

import com.google.gson.Gson;
import com.project.react_tft.security.filter.exception.RefreshTokenException;
import com.project.react_tft.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {
    private final String refreshToken;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("-----------------------------------------------------------------------------------------");
        String path = request.getRequestURI();
        log.info("path: " + path);


        if(!path.contains("refreshToken")) {
            log.info("skip refresh token filter..........");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("리패레쉬토큰 필터 ㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱ");

        //전송된 JSON에서 accessToken과 refreshToken을 얻어온다.
        Map<String, String> tokens = parseRequstJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("엑세스토큰 : : : " + accessToken);
        log.info("리프레쉬토큰 : : : " + refreshToken);

        try{
            checkAccessToken(accessToken);
        }catch (RefreshTokenException refreshTokenException){
            refreshTokenException.sendResponseError(response);
            return; //더이상 실행할 필요 없음.
        }

        Map<String, Object> refreshClaims = null;

        try{
            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);

            //refresh Token의 유효시간이 얼마 안남은경우
            Long exp = (Long) refreshClaims.get("exp");
            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() + 1000);
            Date current = new Date(System.currentTimeMillis());

            // 만료기간과 현재 시간의 간격 계산
            //만일 3일 미만인 경우에는 Refresh Token도 다시 생성.
            long gapTime = (expTime.getTime() - current.getTime());

            log.info("-----------------------------------------------------");
            log.info("현재 시간현재 시간 : " + current);
            log.info("만료 시간만료 시간 : " + expTime);
            log.info("유효 시간유효 시간 : " + gapTime);

            String mid = (String) refreshClaims.get("mid");

            //이 상태까지 오면 무조건 AccessToken은 새로 생성
            String accessTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 1);
            String refreshTokenValue = tokens.get("refreshToken");

            //RefreshToken이 3일도 안남았다면
            if(gapTime < (1000 * 60 * 60 * 3)){
                log.info("새로운 리프레쉬 토큰을 발급했음.");
                refreshTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 30);
            }

            log.info("리프레쉬토큰 result");
            log.info("엑쎄스 토큰 Value " + accessTokenValue);
            log.info("리프레쉬토큰 Value " + refreshTokenValue);

            sendTokens(accessTokenValue, refreshTokenValue, response);


        }catch (RefreshTokenException refreshTokenException){
            refreshTokenException.sendResponseError(response);
            return; //더이상 실행할 코드가 없음.
        }
    }

    private Map<String, String> parseRequstJSON(HttpServletRequest request) {
        //JSON 데이터를 분석해서 mid, mpw 전달 값을 Map으로 처리
    try(Reader reader = new InputStreamReader(request.getInputStream())){
        Gson gson = new Gson();
        return gson.fromJson(reader, Map.class);
    }catch (Exception e){
        log.error(e.getMessage());
        log.error("에러메세지이이이");
    }
    return null;
    }

    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try{
            jwtUtil.validateToken(accessToken);
        }catch (ExpiredJwtException expiredJwtException){
            log.info("Access token expired 만료된 토큰임.");
        }catch (Exception exception){
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        try{
            Map<String, Object> values = jwtUtil.validateToken(refreshToken);
            return values;
        }catch (ExpiredJwtException expiredJwtException){
            throw  new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        }catch (MalformedJwtException malformedJwtException){
            log.error("말폼JWT오류임......................");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }catch (Exception exception){
            new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
        return null;
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response){
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue));

        try {
            response.getWriter().println(jsonStr);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}

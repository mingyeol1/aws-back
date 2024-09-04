package com.project.react_tft.security.filter;

import com.project.react_tft.security.CustomUserDetailsService;
import com.project.react_tft.security.filter.exception.AccessTokenException;
import com.project.react_tft.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        //토큰값이 없어도 들어갈 수 있는 페이지
        if (path.equals("/api/auth/signUp") || path.equals("/api/auth/login") || path.equals("/view") )  {
            filterChain.doFilter(request, response);
            return;
        }
        
        //경로가 api로 시작해야 해당 필터를 거치면서 authentication 값에 user정보가 실려있음 
        if(!path.startsWith("/api")){
            filterChain.doFilter(request, response);
            return;
        }
        log.info("토큰필터 체인입니다~~");
        log.info("JWTUtil 값입니다~~" + jwtUtil);

        try{
            Map<String, Object> payload = validateAccessToken(request);

            String mid = (String) payload.get("mid");
            log.info("midmidmidmid : " + mid);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(mid);

            //등록 사용자 인증 정보 생성.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);
        }catch (AccessTokenException accessTokenException){
            accessTokenException.sendResponseError(response);
            log.error("엑세스토큰오류인데요??" + accessTokenException);
        }


    }

    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {

        String headerStr = request.getHeader("Authorization");

        if (headerStr == null || headerStr.length() < 8){
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        //Bearer 생략
        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7);

        if (tokenType.equalsIgnoreCase("Bearer") == false){
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try{
            Map<String, Object> values = jwtUtil.validateToken(tokenStr);

            return values;
        } catch (MalformedJwtException malformedJwtException){
            log.info("말폼제떱티오류말폼제떱티오류말폼제떱티오류말폼제떱티오류");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        }catch (SignatureException signatureException){
            log.info("시그니쳐오류시그니쳐오류시그니쳐오류시그니쳐오류시그니쳐오류");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        }catch (ExpiredJwtException expiredJwtException){
            log.info("만료된토큰만료된토큰만료된토큰만료된토큰만료된토큰만료된토큰");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
    }
}

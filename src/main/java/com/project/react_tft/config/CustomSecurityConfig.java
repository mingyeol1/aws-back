package com.project.react_tft.config;


import com.project.react_tft.security.CustomSocialLoginSuccessHandler;
import com.project.react_tft.security.CustomUserDetailsService;
import com.project.react_tft.security.filter.LoginFilter;
import com.project.react_tft.security.filter.TokenCheckFilter;
import com.project.react_tft.security.filter.handler.UserLoginSuccessHandler;
import com.project.react_tft.service.MemberService;
import com.project.react_tft.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@Log4j2
@EnableMethodSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final MemberService memberService; // 추가 필요

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("---------------web configure-------------------");
        return (web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("---------------------------configure-------------------------------");

        // CSRF 설정 및 기본 요청 권한 설정
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/login", "/api/auth/signUp", "/api/meet/list", "/view/").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS Preflight 요청 허용
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // AuthenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);

        // LoginFilter 설정
        LoginFilter loginFilter = new LoginFilter("/api/auth/login", memberService);
        loginFilter.setAuthenticationManager(authenticationManager);

        // 성공 핸들러 설정
        UserLoginSuccessHandler successHandler = new UserLoginSuccessHandler(jwtUtil);
        loginFilter.setAuthenticationSuccessHandler(successHandler);

        // 필터 순서 설정 - 중요!
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(tokenCheckFilter(jwtUtil, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        // OAuth2 설정
        http.oauth2Login(oauth2 -> {
            oauth2.loginPage("/api/auth/login");
            oauth2.successHandler(authenticationSuccessHandler());
        });

        // CORS 설정
        http.cors(cors ->
                cors.configurationSource(corsConfigurationSource())
        );

        // Remember-me 설정
        http.rememberMe(rememberMe -> {
            rememberMe.key("12345678")
                    .tokenRepository(persistentTokenRepository())
                    .userDetailsService(customUserDetailsService)
                    .tokenValiditySeconds(60 * 60 * 24 * 30);
        });

        return http.build();
    }

    // CORS 필터 Bean 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "https://www.tft.p-e.kr"));
        corsConfiguration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder, jwtUtil);
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        return new TokenCheckFilter(jwtUtil, customUserDetailsService);
    }
}
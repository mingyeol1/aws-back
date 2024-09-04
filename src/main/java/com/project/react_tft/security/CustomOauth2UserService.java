package com.project.react_tft.security;

import com.project.react_tft.Repository.MemberRepository;
import com.project.react_tft.domain.Member;
import com.project.react_tft.domain.MemberRole;
import com.project.react_tft.dto.MemberSecurityDTO;
import com.project.react_tft.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("User Request....." + userRequest);
        log.info(userRequest);

        log.info("oauth2 user....................................");
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();
        log.info("Name : " + clientName); // 어떤 소셜을 사용했는지 확인

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String memail = null;
        String mnick = null; // 닉네임

        switch (clientName) {
            case "kakao":
                memail = getKakaoEmail(paramMap);
                mnick = getKakaoNickName(paramMap); // 닉네임을 가져오는 메서드
                break;
        }

        log.info("===============================================");
        log.info(memail);
        log.info(mnick);
        log.info("===============================================");
        return generateDTO(mnick, memail, paramMap); // MemberSecurityDTO로 반환 처리
    }

    private MemberSecurityDTO generateDTO(String mnick, String memail, Map<String, Object> parmas) {
        Optional<Member> result = memberRepository.findByMemail(memail);

        // 데이터베이스에 해당 이메일 사용자가 없는 경우...
        if (result.isEmpty()) {
            // 회원 추가... mid는 이메일 주소 / 패스워드 1111
            Member member = Member.builder()
                    .mid(memail)
                    .mpw(passwordEncoder.encode("1111"))
                    .memail(memail)
                    .mnick(mnick)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            memberRepository.save(member);

            // MemberSecurityDTO 로 반환
            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                    memail,
                    "1111",
                    mnick,
                    memail,
                    false,
                    true,
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            memberSecurityDTO.setProps(parmas);
            return memberSecurityDTO;
        } else {
            Member member = result.get();
            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                    member.getMid(),
                    member.getMpw(),
                    member.getMnick(),
                    member.getMemail(),
                    member.isDel(),
                    member.isSocial(),
                    member.getRoleSet().stream()
                            .map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name()))
                            .collect(Collectors.toList())
            );
            return memberSecurityDTO;
        }
    }

    // getKakaoEmail() 만들기... : KAKAO에서 전달된 정보를 통해서 Email 반환 처리
    private String getKakaoEmail(Map<String, Object> paramMap) {
        log.info("Kakao ....................................");
        Object value = paramMap.get("kakao_account");
        log.info("----------------------" + value);

        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String) accountMap.get("email");
        log.info("Email................... : " + email);
        return email;
    }

    // getKakaoNickName() 만들기... : KAKAO에서 전달된 정보를 통해서 닉네임 반환 처리
    private String getKakaoNickName(Map<String, Object> paramMap) {
        Object value = paramMap.get("kakao_account");
        if (value instanceof LinkedHashMap) {
            LinkedHashMap accountMap = (LinkedHashMap) value;
            LinkedHashMap profileMap = (LinkedHashMap) accountMap.get("profile");
            String nickName = (String) profileMap.get("nickname");  // 카카오에서 제공하는 닉네임 필드
            log.info("Nick Name................... : " + nickName);
            return nickName;
        }
        return null; // 닉네임이 없을 경우 null 반환
    }
}

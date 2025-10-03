package com.project.react_tft.security;

import com.project.react_tft.Repository.MemberRepository;
import com.project.react_tft.domain.Member;
import com.project.react_tft.dto.MemberSecurityDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {
        log.info("loadUserByUsername : " + mid);


       Member member = memberRepository.findById(mid).orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않음."));

//        log.info("여기는 닿습니까??");



        if (member.getRoleSet().isEmpty()){
            log.error("권한이 없는데요?????????????????????");
            throw new UsernameNotFoundException("권한이 없는데요?????????????????????");
        }

        //UserDetails 객체로 반환하는 userDetails를 생성...
        MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                member.getMid(),
                member.getMpw(),
                member.getMemail(),
                member.getMnick(),
                member.isDel(),
                false,  //소셜로 로그인 처리하지 않는 상황
                member.getRoleSet()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList())
        );

        log.info("memberSecurityDTO : " + memberSecurityDTO);
        log.info(memberSecurityDTO);
        return memberSecurityDTO;
    }
}

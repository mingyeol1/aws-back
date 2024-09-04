package com.project.react_tft.controller;

import com.project.react_tft.Repository.MemberRepository;
import com.project.react_tft.domain.Member;
import com.project.react_tft.dto.MemberDTO;
import com.project.react_tft.security.CustomUserDetailsService;
import com.project.react_tft.service.MemberService;
import com.project.react_tft.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {
    private final MemberService memberService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody MemberDTO memberDTO) {
        try {
            Member member = memberService.signUp(memberDTO);
            return ResponseEntity.ok(member);
        } catch (MemberService.MemberMidExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입중 오류발생...");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO dto) {
        Member member = memberService.login(dto.getMid(), dto.getMpw());

        if (member != null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(dto.getMid());

            // 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 인증 설정
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // Payload 값 보내기
            Map<String, Object> claim = new HashMap<>();
            claim.put("mid", member.getMid());
            claim.put("mpw", member.getMpw());
            claim.put("role", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            String accessToken = jwtUtil.generateToken(claim, 1);
            String refreshToken = jwtUtil.generateToken(claim, 30);

            Map<String, String> tokens = Map.of("accessToken", accessToken, "refreshToken", refreshToken);

            return ResponseEntity.ok(tokens);
        } else {
            log.info("아이디 없을지도.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 및 비밀번호 오류임.");
        }
    }

    @GetMapping("/modify")
    public ResponseEntity<?> modify() {
        log.info("modify..........................");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        log.info(principal);
        log.info(authentication.getName());
        log.info(authentication.getAuthorities());

        Member detail = memberService.getDetail(authentication.getName());
        log.info(detail);

        return ResponseEntity.ok(detail);
    }


    @PutMapping("/modify")
    public ResponseEntity<?> modifyPost(@RequestBody MemberDTO dto) {
        log.info("modifyPost..........................");
        log.info("memberJoinDTO.........................." + dto);

        try {
            memberService.modify(dto);
            return ResponseEntity.ok("회원정보 수정이 완료되었습니다.");
        } catch (MemberService.MemberMidExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("닉네임 및 이메일이 이미 존재합니다.");
        }
    }

    @PostMapping("/checkPw")
    public ResponseEntity<?> checkPw(@RequestBody Map<String, String> request) {
        String mpw = request.get("mpw");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mid = authentication.getName();

        Optional<Member> optionalMember = memberRepository.findById(mid);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (passwordEncoder.matches(mpw, member.getMpw())) {
                log.info("비밀번호가 일치");
                return ResponseEntity.ok("비밀번호가 일치.");
            } else {
                log.info("비밀번호가 노일치.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 노일치.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보가 없음.");
        }
    }


    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody Map<String, String> request) {
        String mpw = request.get("mpw");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mid = authentication.getName();

        Optional<Member> optionalMember = memberRepository.findById(mid);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (passwordEncoder.matches(mpw, member.getMpw())) {
                memberService.remove(mid);
                return ResponseEntity.ok("성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 다릅니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보가 없음.");
        }
    }


}


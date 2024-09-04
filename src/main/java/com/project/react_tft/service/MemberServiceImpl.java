package com.project.react_tft.service;

import com.project.react_tft.Repository.MemberRepository;
import com.project.react_tft.domain.Member;
import com.project.react_tft.domain.MemberRole;
import com.project.react_tft.dto.MemberDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Member signUp(MemberDTO dto) throws MemberMidExistException{
        String mpw = dto.getMpw();
        String chekcmpw = dto.getCheckMpw();
        if(memberRepository.existsById(dto.getMid())){
            log.info("이미 있는 아이디인데요.");
            throw new MemberMidExistException();
        }
        if (memberRepository.existsByMemail(dto.getMemail())){
            log.info("이미 있는 이메일인데요");
            throw new MemberMidExistException();
        }

        if(!mpw.equals(chekcmpw)) {
            log.info("비밀번호가 노일치");
            return null;
        }

        Member member = modelMapper.map(dto, Member.class);

        member.setMpw(passwordEncoder.encode(dto.getMpw()));
        member.addRole(MemberRole.USER);

        memberRepository.save(member);

        return member;
    }



    @Override
    public Member login(String mid, String mpw) {
        Optional<Member> member = memberRepository.findById(mid);

        if (member.isPresent()) {
            if (passwordEncoder.matches(mpw, member.get().getMpw())) {
                return member.get();
            } else {
                log.info("비밀번호가 틀렸는데요.");
                return null;
            }
        } else {
            log.info("아이디가 없는데요.");
            return null;
        }
    }

    @Override
    public void modify(MemberDTO memberDTO) throws MemberMidExistException {
        log.info("회원정보를 수정하겠음----------------------------------.");

        Optional<Member> optionalMember = memberRepository.findById(memberDTO.getMid());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            // 현재 유저의 닉네임과 이메일을 제외하고 중복 검사
            // 소셜로그인시 수정하면 nullPointerException 때문에 member.getMnick() != null 를 넣어줌.
            if (member.getMnick() != null && !member.getMnick().equals(memberDTO.getMnick()) && memberRepository.existsByMnick(memberDTO.getMnick())) {
                log.info("이미 있는 닉네임인데요");
                throw new MemberMidExistException();
            }

            if (!member.getMemail().equals(memberDTO.getMemail()) && memberRepository.existsByMemail(memberDTO.getMemail())){
                log.info("이미 있는 이메일인데요");
                throw new MemberMidExistException();
            }

            // 기존 비밀번호를 임시로 저장
            String existingPassword = member.getMpw();

            // DTO의 데이터를 엔티티에 매핑
            modelMapper.map(memberDTO, member);

            // 비밀번호가 비어 있거나 기존 비밀번호와 같으면 기존 비밀번호 유지
            if (memberDTO.getMpw() == null || memberDTO.getMpw().isEmpty() || passwordEncoder.matches(memberDTO.getMpw(), existingPassword)) {
                member.setMpw(existingPassword);
            } else {
                // 새 비밀번호를 인코딩하여 저장
                member.setMpw(passwordEncoder.encode(memberDTO.getMpw()));
            }

            log.info("회원정보를 수정했음 ->" + member);

            memberRepository.save(member);
        } else {
            log.info("회원 정보를 찾을 수 없음: ID=" + memberDTO.getMid());
            throw new RuntimeException("회원정보가 없는데요?");
        }
    }


    @Override
    public Member getDetail(String mid) {
        Optional<Member> result = memberRepository.findById(mid);
        log.info("개인정보인데요?????");
        if (result.isPresent()) {
            Member member = result.get();
            log.info("Member service impl getDetail --------------------------------------");
            log.info(member);
            return member;
        } else {
            throw new NoSuchElementException("비어있음...................... " + mid);
        }
    }

    @Override
    public void remove(String mid) {
        log.info("회원을 삭제 하겠음.");
        log.info(mid);
        Member member = getDetail(mid);
        member.changeDel(true);

        log.info(member + "를 삭제하겠음.");

        memberRepository.save(member);
    }
}

package com.project.react_tft.service;

import com.project.react_tft.domain.Member;
import com.project.react_tft.dto.MemberDTO;

public interface MemberService {

    class MemberMidExistException extends Exception {
        public MemberMidExistException() {}
    }

    Member signUp(MemberDTO dto) throws MemberMidExistException;

    Member login(String mid, String mpw);

    void remove(String mid);
    void modify(MemberDTO memberDTO) throws MemberMidExistException;

    Member getDetail(String mid);
}

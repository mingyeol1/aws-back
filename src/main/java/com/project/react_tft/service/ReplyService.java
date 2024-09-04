package com.project.react_tft.service;

import com.project.react_tft.dto.PageRequestDTO;
import com.project.react_tft.dto.PageResponseDTO;
import com.project.react_tft.dto.ReplyDTO;

import java.util.NoSuchElementException;

public interface ReplyService {

    Long register(ReplyDTO replyDTO);

    ReplyDTO read(Long rno);

    void modify(ReplyDTO replyDTO);

    void remove(Long rno) throws NoSuchElementException;

    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO);
}

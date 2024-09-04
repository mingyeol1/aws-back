package com.project.react_tft.service;

import com.project.react_tft.domain.Board;
import com.project.react_tft.dto.*;

public interface BoardService {

    Long register(BoardDTO boardDTO);

    BoardDTO readOne(Long bno);

    void modify(BoardDTO boardDTO);

    void remove(Long bno);

    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);

    // 댓글의 숫자까지 처리
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);


    PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO);
}

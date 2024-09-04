package com.project.react_tft.service;

import com.project.react_tft.domain.MeetReply;
import com.project.react_tft.dto.MeetReplyDTO;
import com.project.react_tft.dto.PageRequestDTO;
import com.project.react_tft.dto.PageResponseDTO;
import com.project.react_tft.dto.ReplyDTO;

import java.util.NoSuchElementException;

public interface MeetReplyService {
    Long register(MeetReplyDTO meetReplyDTO);

    MeetReplyDTO read(Long meetRid);

    void modify(MeetReplyDTO meetReplyDTO);

    void remove(Long meetRid);

    PageResponseDTO<MeetReplyDTO> getListOfMeetBoard(Long meetId, PageRequestDTO pageRequestDTO);
}

package com.project.react_tft.service;

import com.project.react_tft.Repository.MeetReplyRepository;
import com.project.react_tft.domain.MeetBoard;
import com.project.react_tft.domain.MeetReply;
import com.project.react_tft.domain.Reply;
import com.project.react_tft.dto.MeetReplyDTO;
import com.project.react_tft.dto.PageRequestDTO;
import com.project.react_tft.dto.PageResponseDTO;
import com.project.react_tft.dto.ReplyDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MeetReplyServiceImpl implements MeetReplyService {

    private final MeetReplyRepository replyRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(MeetReplyDTO meetReplyDTO) {
        MeetBoard meetBoard = MeetBoard.builder()
                .meetId(meetReplyDTO.getMeetId())
                .build();

        MeetReply meetReply = MeetReply.builder()
                .meetBoard(meetBoard)
                .replyText(meetReplyDTO.getReplyText())
                .replyer(meetReplyDTO.getReplyer())
                .build();

        Long meetRid = replyRepository.save(meetReply).getMeetRid();

        return meetRid;
    }

    @Override
    public MeetReplyDTO read(Long meetRid) {

        Optional<MeetReply> meetReplyOptional = replyRepository.findById(meetRid);

        MeetReply meetReply = meetReplyOptional.orElseThrow();

        // ModelMapper로 매핑
        MeetReplyDTO meetReplyDTO = modelMapper.map(meetReply, MeetReplyDTO.class);

        // meetId를 수동으로 설정
        meetReplyDTO.setMeetId(meetReply.getMeetBoard().getMeetId());

        return meetReplyDTO;
    }


    @Override
    public void modify(MeetReplyDTO meetReplyDTO) {
        Optional<MeetReply> meetReplyOptional = replyRepository.findById(meetReplyDTO.getMeetRid());

        MeetReply meetReply = meetReplyOptional.orElseThrow();

        meetReply.changeText(meetReplyDTO.getReplyText());

        replyRepository.save(meetReply);
    }


    @Override
    public void remove(Long meetRid) {
        replyRepository.deleteById(meetRid);
    }

    @Override
    public PageResponseDTO<MeetReplyDTO> getListOfMeetBoard(Long meetId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("meetRid").ascending());  // 처음 댓글을 위로 올리기 위해서...

        Page<MeetReply> result = replyRepository.listOfMeetBoard(meetId, pageable);

        List<MeetReplyDTO> dtoList = result.getContent().stream().map(meetReply -> {
            MeetReplyDTO dto = modelMapper.map(meetReply, MeetReplyDTO.class);
            // meetId를 수동으로 설정
            dto.setMeetId(meetReply.getMeetBoard().getMeetId());
            return dto;
        }).collect(Collectors.toList());

        return PageResponseDTO.<MeetReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

}

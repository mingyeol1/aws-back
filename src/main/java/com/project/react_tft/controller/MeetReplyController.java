package com.project.react_tft.controller;

import com.project.react_tft.dto.MeetReplyDTO;
import com.project.react_tft.dto.PageRequestDTO;
import com.project.react_tft.dto.PageResponseDTO;
import com.project.react_tft.service.MeetReplyService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetreplyes")
public class MeetReplyController {
    private final MeetReplyService meetReplyService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MeetReplyDTO meetReplyDTO) {
        meetReplyService.register(meetReplyDTO);

        return ResponseEntity.ok("댓글 작성완료");
    }

    @GetMapping("/list/{meetId}")
    public PageResponseDTO<MeetReplyDTO> getList(@PathVariable("meetId") Long meetId,
                                                PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MeetReplyDTO> responseDTO = meetReplyService.getListOfMeetBoard(meetId, pageRequestDTO);

        return responseDTO;
    }

    @GetMapping("/{meetRid}")
    public MeetReplyDTO getReplyDTO(@PathVariable("meetRid") Long meetRid) {

        MeetReplyDTO meetReplyDTO = meetReplyService.read(meetRid);

        return meetReplyDTO;
    }

    @DeleteMapping("/{meetRid}")
    public Map<String, Long> remove(@PathVariable("meetRid") Long meetRid) {

        meetReplyService.remove(meetRid);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("meetRid", meetRid);

        return resultMap;

    }

    @PutMapping("/{meetRid}")
    public Map<String,Long> remove(@PathVariable("meetRid")Long meetRid, @RequestBody MeetReplyDTO meetReplyDTO) {

        meetReplyDTO.setMeetRid(meetRid);
        meetReplyService.modify(meetReplyDTO);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("meetRid", meetRid);

        return resultMap;
    }

}

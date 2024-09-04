package com.project.react_tft.controller;

import com.project.react_tft.dto.PageRequestDTO;
import com.project.react_tft.dto.PageResponseDTO;
import com.project.react_tft.dto.ReplyDTO;
import com.project.react_tft.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor    // 의존성 주입을 위한
public class ReplyController {

    private final ReplyService replyService;

    @Operation(summary = "Replies Post - Post 방식으로 댓글 등록")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> register(
            @Valid @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult)throws BindException {

        log.info(replyDTO);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();

        Long rno = replyService.register(replyDTO);
        resultMap.put("rno", rno);

        return resultMap;
    }

    @Operation(summary = "Replies of Board로 Get방식으로 특정 게시글 댓글 목록 처리..")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno,
                                            PageRequestDTO pageRequestDTO){
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno,pageRequestDTO);

        return responseDTO;
    }

    @Operation(summary = "Read Reply - GET방식으로 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno){
        ReplyDTO replyDTO = replyService.read(rno);
        return replyDTO;
    }

    @Operation(summary = "Delete Reply - DELETE 메서드를 이용한 댓글 삭제")
    @DeleteMapping("/{rno}")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable("rno") Long rno) {
        log.info("Reply DELETE Controller...................");
        try {
            replyService.remove(rno);
            Map<String, Object> result = new HashMap<>();
            result.put("rno", rno);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Modify Reply - PUT 방식으로 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> modify(
            @PathVariable("rno") Long rno,
            @RequestBody ReplyDTO replyDTO){
        replyDTO.setBno(rno);   //번호일치
        replyService.modify(replyDTO);
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }

}

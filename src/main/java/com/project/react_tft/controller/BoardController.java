package com.project.react_tft.controller;


import com.project.react_tft.dto.*;
import com.project.react_tft.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

//    @GetMapping("/list")
//    public void list(PageRequestDTO pageRequestDTO, Model model) {
//
////        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
//        PageResponseDTO<BoardListReplyCountDTO> responseDTO =
//                                    boardService.listWithReplyCount(pageRequestDTO);
//
//        log.info(responseDTO);
//
//        model.addAttribute("responseDTO",responseDTO);
//    }

    @Operation(summary = "REST방식 Board Get방식으로 게시글 처리")
    @GetMapping(value = "/list")
    public ResponseEntity<PageResponseDTO<BoardDTO>> getList(PageRequestDTO pageRequestDTO){
//        PageResponseDTO<BoardDTO> responseDTO = boardService.getList(pageRequestDTO);
        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/register")
    public void registerGET() {

    }


//    @PostMapping("/register")
//    public String registerPost(
//            @Valid BoardDTO boardDTO,
//            BindingResult bindingResult,
//            RedirectAttributes redirectAttributes) {
//        log.info("board POST register.......");
//        // 값 검증 이후 확인....
//        if(bindingResult.hasErrors()) {   //검증시 에러 있는 경우...
//            log.info("has Errors..... ");
//            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
//            return "redirect:/board/register";
//        }
//        // 등록 작업
//        log.info(boardDTO);
//        Long bno = boardService.register(boardDTO);
//        redirectAttributes.addFlashAttribute("result", bno);
//        return "redirect:/board/list";
//    }

    @Operation(summary = "board Post - Post 방식으로 게시글 등록")
    @PostMapping(value = "/register")
    public ResponseEntity<String> register(@RequestBody BoardDTO boardDTO){
        log.info("board POST register...  : "+boardDTO);
        Long bno = boardService.register(boardDTO);
        return ResponseEntity.ok(bno.toString());
    }


//    @PreAuthorize("isAuthenticated()")  //인증된 사용자만 접근 가능..
//    @GetMapping({"/read","/modify"})  //조회
//    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model) {
//        BoardDTO boardDTO = boardService.readOne(bno);
//        log.info(boardDTO);
//        model.addAttribute("dto", boardDTO);
//    }
    @Operation(summary = "board Read - GET 방식으로 게시글 1개 조회")
    @GetMapping("/read/{bno}")  //조회
    public ResponseEntity<BoardDTO> read(@PathVariable("bno") Long bno) {
        log.info("test~!!!!99999999999999999999999999999999999999999999");
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info(boardDTO);
    //        model.addAttribute("dto", boardDTO);
        return ResponseEntity.ok(boardDTO);
    }

    @Operation(summary = "Modify Board - PUT 방식으로 댓글 수정")
//    @PostMapping("/modify")
    @PutMapping(value = "/{bno}")
    public ResponseEntity<Map<String, Long>> modify(@PathVariable("bno") Long bno,
                         @Valid @RequestBody BoardDTO boardDTO) {
        log.info("HarryPotter. " + boardDTO);

        boardDTO.setBno(bno);   // 이걸 왜 쓰는거지?
        boardService.modify(boardDTO);
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("bno", bno);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }


//    @Operation(summary = "Modify Reply - PUT 방식으로 댓글 수정")
//    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String, Long> modify(
//            @PathVariable("rno") Long rno,
//            @RequestBody ReplyDTO replyDTO){
//        replyDTO.setBno(rno);   //번호일치// ReplyDTO에 setter가 없는데 setBno()을 사용할 수 있는 이유?
//        replyService.modify(replyDTO);
//        Map<String, Long> resultMap = new HashMap<>();
//        resultMap.put("rno", rno);
//        return resultMap;
//    }




//    @PostMapping("/remove")
//    public String remove(Long bno, RedirectAttributes redirectAttributes) {
//        log.info("remove post.... "+ bno);
//        boardService.remove(bno);
//        redirectAttributes.addFlashAttribute("result","removed");
//        return "redirect:/board/list";
//    }

    @Operation(summary = "Delete Board - DELETE 메서드를 이용한 댓글 삭제")
    @DeleteMapping("/{bno}")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable("bno") Long bno) {
        log.info("Reply DELETE Controller...................");
            boardService.remove(bno);
            Map<String, Object> result = new HashMap<>();
            result.put("bno", bno);
            return new ResponseEntity<>(result, HttpStatus.OK);
    }


}

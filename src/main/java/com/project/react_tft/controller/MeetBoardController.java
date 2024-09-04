package com.project.react_tft.controller;

import com.project.react_tft.domain.MeetBoard;
import com.project.react_tft.domain.Member;
import com.project.react_tft.dto.*;
import com.project.react_tft.dto.image.ImageResultDTO;
import com.project.react_tft.service.MeetBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meet")
public class MeetBoardController {

    private final MeetBoardImageController meetBoardImageController;
    @Value("C:\\upload")
    private String uploadPath;

    private final MeetBoardService meetBoardService;


//    @GetMapping(value = "/list")
////    public ResponseEntity<PageResponseDTO<MeetBoardDTO>> getList(PageRequestDTO pageRequestDTO) {
//    public ResponseEntity<PageResponseDTO<MeetBoardListReplyCountDTO>> getList(PageRequestDTO pageRequestDTO) {
//        log.info("리스트에 접근했음");
//        //댓글 페이지 기능 넣은건데 안돼면 주석 지우고 사용
////        PageResponseDTO<MeetBoardDTO> responseDTO = meetBoardService.list(pageRequestDTO);
//
//        PageResponseDTO<MeetBoardListReplyCountDTO> responseDTO = meetBoardService.listWithReplyCount(pageRequestDTO);
//        return ResponseEntity.ok(responseDTO);
//    }

    @GetMapping(value = "/list")
    public ResponseEntity<PageResponseDTO<MeetBoardListAllDTO>> getList(PageRequestDTO pageRequestDTO) {
        log.info("리스트에 접근했음");
        log.info("Authorization 헤더: {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("PageRequestDTO: {}", pageRequestDTO);

        PageResponseDTO<MeetBoardListAllDTO> responseDTO = meetBoardService.listWithAll(pageRequestDTO);

        log.info("Response DTO: {}", responseDTO);
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/register")
    public ResponseEntity<?> getRegister() {
        log.info("meet board register 접근완료 ");

        return ResponseEntity.status(200).body("접근완료");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MeetBoardDTO meetBoardDTO, @RequestPart(value = "files", required = false) List<MultipartFile> files){

        // 게시글 등록
        meetBoardService.registerMeet(meetBoardDTO);

        return ResponseEntity.status(200).body("작성완료");
    }




    @GetMapping("/modify/{meetNum}")
    public ResponseEntity<?> getModify(@PathVariable Long meetNum){

        MeetBoardDTO meetBoardDTO = meetBoardService.getDetail(meetNum);

        return ResponseEntity.ok(meetBoardDTO);
    }


    @PutMapping("/modify/{meetNum}")
    public ResponseEntity<?> modify(@RequestBody MeetBoardDTO meetBoardDTO){

        meetBoardService.modify(meetBoardDTO);

        return ResponseEntity.ok(meetBoardDTO);
    }

    @GetMapping("/read/{meetNum}")
    public ResponseEntity<?> read(@PathVariable Long meetNum){

        MeetBoardDTO meetBoardDTO = meetBoardService.getDetail(meetNum);


        return ResponseEntity.ok(meetBoardDTO);

    }

    @DeleteMapping("/delete/{meetNum}")
    public ResponseEntity<?> delete(@PathVariable Long meetNum) {

        // meetNum에 해당하는 게시글 정보를 가져옴
        MeetBoardDTO meetBoardDTO = meetBoardService.getDetail(meetNum);

        // 파일 이름 목록을 가져옴
        List<String> fileNames = meetBoardDTO.getFileNames();

        // 파일이 존재하면 삭제
        if (fileNames != null && fileNames.size() > 0) {
            removeFiles(fileNames);
        }

        // 게시글 삭제
        meetBoardService.remove(meetNum);

        return ResponseEntity.ok("삭제완료");
    }

    public void removeFiles(List<String> files){

        for (String fileName:files) {

            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();


            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();


                if (contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
    }


}

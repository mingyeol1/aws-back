package com.project.react_tft.service;

import com.project.react_tft.Repository.MeetBoardRepository;
import com.project.react_tft.domain.Board;
import com.project.react_tft.domain.MeetBoard;
import com.project.react_tft.domain.Member;
import com.project.react_tft.dto.*;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MeetboardServiceImpl implements MeetBoardService {

    private final MeetBoardRepository meetBoardRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long registerMeet(MeetBoardDTO meetBoardDTO) {
        MeetBoard meetBoard = dtoToEntity(meetBoardDTO);
        meetBoardRepository.save(meetBoard);
        return meetBoard.getMeetId();
    }

    @Override
    public void modify(MeetBoardDTO meetBoardDTO) {
        Optional<MeetBoard> result = meetBoardRepository.findById(meetBoardDTO.getMeetId());
        if (result.isPresent()) {
            MeetBoard meetBoard = result.get();
            modelMapper.map(meetBoardDTO, meetBoard);
            meetBoardRepository.save(meetBoard);

            if(meetBoardDTO.getFileNames() != null){
                for (String fileName : meetBoardDTO.getFileNames()) {
                    String[] arr = fileName.split("_");
                    meetBoard.addImage(arr[0], arr[1]);
                }
            }

            log.info("meet 수정완료");
        } else {
            log.info("게시물이 없는데요?");
            throw new RuntimeException("게시글이 없는데요?");
        }
    }

    @Override
    public MeetBoardDTO getDetail(Long meetId) {
        Optional<MeetBoard> result = meetBoardRepository.findByIdWithImages(meetId);
        log.info("게시글을를 불러오겠음");
        if (result.isPresent()) {
            MeetBoard meetBoard = result.get();
            MeetBoardDTO meetBoardDTO = entityToDTO(meetBoard);
            log.info("상세 개시물을 불러왔음");
            log.info(meetId);
            return meetBoardDTO;
        } else {
            throw new NoSuchElementException("비어있음...................... " + meetId);
        }
    }



    @Override
    public void remove(Long meetId) {
        meetBoardRepository.deleteById(meetId);
    }

    @Override
    public PageResponseDTO<MeetBoardDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("meetId");

        Page<MeetBoard> result = meetBoardRepository.searchAll(types, keyword, pageable);

        // 변환... MeetBoard -> MeetBoardDTO
        List<MeetBoardDTO> dtoList = result.getContent().stream()
                .map(meetBoard -> modelMapper.map(meetBoard, MeetBoardDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<MeetBoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<MeetBoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("meetId");

        Page<MeetBoardListReplyCountDTO> result = meetBoardRepository.searchWithMeetReplyCount(types, keyword, pageable);

        return PageResponseDTO.<MeetBoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();

    }

    @Override
    public PageResponseDTO<MeetBoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("meetId");

        Page<MeetBoardListAllDTO> result = meetBoardRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<MeetBoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

}




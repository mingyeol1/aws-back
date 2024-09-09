package com.project.react_tft.controller;

import com.project.react_tft.Repository.ReviewRepository;
import com.project.react_tft.domain.Review;
import com.project.react_tft.dto.*;
import com.project.react_tft.service.MeetBoardService;
import com.project.react_tft.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/public/review")
@RequiredArgsConstructor
public class UnloginController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final MeetBoardService meetBoardService;

    @PostMapping("/listOfReviewPaginated")
    public ReviewPageResponseDTO<ReviewDTO> listOfReviewPaginated(@RequestBody ReviewPageRequestDTO requestDTO) {
        Page<Review> result = reviewService.listOfReviewPaginated(requestDTO.getMovie_id(), requestDTO.getPage(), requestDTO.getSize());
        List<ReviewDTO> resultList = result.getContent().stream().map(review ->
                ReviewDTO.builder()
                        .review_id(review.getReview_id())
                        .movie_id(review.getMovie().getMovie_id())
                        .review_star(review.getReview_star())
                        .review_text(review.getReview_text())
                        .mnick(review.getMember().getMnick())
                        .build()
        ).collect(Collectors.toList());

        int stars = reviewRepository.getSumStarRatingByMovieId(requestDTO.getMovie_id());

        return ReviewPageResponseDTO.<ReviewDTO>withAll()
                .reviewPageRequestDTO(requestDTO)
                .dtoList(resultList)
                .total((int) result.getTotalElements())
                .allStars(stars)
                .build();
    }

    @GetMapping(value = "/list")
    public ResponseEntity<PageResponseDTO<MeetBoardListAllDTO>> getList(PageRequestDTO pageRequestDTO) {
        log.info("리스트에 접근했음");
        log.info("Authorization 헤더: {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("PageRequestDTO: {}", pageRequestDTO);

        PageResponseDTO<MeetBoardListAllDTO> responseDTO = meetBoardService.listWithAll(pageRequestDTO);

        log.info("Response DTO: {}", responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

}
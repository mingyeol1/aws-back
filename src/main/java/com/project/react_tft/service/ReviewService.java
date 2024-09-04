package com.project.react_tft.service;

import com.project.react_tft.domain.Review;
import com.project.react_tft.dto.ReviewDTO;
import com.project.react_tft.dto.ReviewPageRequestDTO;
import com.project.react_tft.dto.ReviewPageResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {

    Logger log = LoggerFactory.getLogger(ReviewService.class);

    class ReviewIdExistException extends Exception {
       public ReviewIdExistException() {
            log.info("ReviewIdExistException");
       }
    }

    Long register(ReviewDTO reviewDTO);
    ReviewDTO readOne(Long reviewId) throws ReviewIdExistException;
    void modify(ReviewDTO reviewDTO) throws ReviewIdExistException;
    void remove(Long bno) throws ReviewIdExistException;

    Page<Review> listOfReviewPaginated(Long movie_id, int page, int size);

}

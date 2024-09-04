package com.project.react_tft.dto;

import com.project.react_tft.domain.Review;
import lombok.*;

import java.util.List;

@Getter
@ToString
public class ReviewPageResponseDTO<E> {

    //리뷰id
    private Long review_id;
    //영화 id
    private Long movie_id;
    //맴버 id
    private String mid;
    //리뷰내용
    private String review_text;
    //별점
    private Integer review_star;

    private int page;
    private int size;
    private int total;
    private int allStars;

    private List<E> dtoList;


    @Builder(builderMethodName = "withAll")
    public ReviewPageResponseDTO(ReviewPageRequestDTO reviewPageRequestDTO, List<E> dtoList , int total, int allStars) {

        if(total <= 0) {
            return;
        }
        this.page = reviewPageRequestDTO.getPage();
        this.size = reviewPageRequestDTO.getSize();
        this.total = total;
        this.allStars = allStars;

        this.dtoList = dtoList;
    }

}

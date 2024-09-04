package com.project.react_tft.dto;

import com.project.react_tft.domain.Member;
import com.project.react_tft.domain.Movie;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

//    private Member member;
//    private Movie movie;

    //리뷰id
    private Long review_id;
    //영화 id
    private Long movie_id;
    //맴버 id


    private String mnick;
    //리뷰내용
    private String review_text;
    //별점
    private Integer review_star;

}

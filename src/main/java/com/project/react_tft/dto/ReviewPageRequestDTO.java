package com.project.react_tft.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPageRequestDTO {
    private Long movie_id;
    private int page;
    private int size;

}

package com.project.react_tft.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    
    //영화id
    //auto increment x
    private Long movie_id;
    //영화제목
    private String movie_title;
}

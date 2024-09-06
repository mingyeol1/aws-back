package com.project.react_tft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetBoardListAllDTO {
    private Long meetId;
    private int personnel;
    private LocalDateTime meetTime;
    private String meetTitle;
    private String meetWriter;
    private String meetContent;
    private LocalDateTime regDate;
    private Long replyCount;
    private List<String> imageUrls; // S3 URL을 저장할 필드

    private List<MeetBoardImageDTO> meetBoardImages;
}

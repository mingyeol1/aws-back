package com.project.react_tft.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeetBoardListReplyCountDTO {

    private Long meetId;
    private String meetTitle;
    private String meetWriter;
    private LocalDateTime regDate;

    private Long meetReplyCount;
}

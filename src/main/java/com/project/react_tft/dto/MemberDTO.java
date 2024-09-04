package com.project.react_tft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private String mid;
    private String mpw;
    private String checkMpw;
    private String mname;
    private String mnick;
    private String memail;
    private String mphone;
    private boolean del;

}

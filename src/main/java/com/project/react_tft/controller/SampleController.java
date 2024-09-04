package com.project.react_tft.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class SampleController {

    @GetMapping("/api/test")
    public String hello() {
        return "테스트입니다.";
    }

}

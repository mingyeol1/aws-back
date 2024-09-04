package com.project.react_tft.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

        //for healthCheck
    @RequestMapping("/")
    public String home(){
        return "서버가 동작중입니다.";
    }

}

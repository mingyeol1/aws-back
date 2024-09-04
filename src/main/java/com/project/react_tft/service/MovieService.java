package com.project.react_tft.service;

import com.project.react_tft.dto.MovieDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MovieService {

    Logger log = LoggerFactory.getLogger(MovieService.class);

    Long register(MovieDTO movieDTO) ;

}

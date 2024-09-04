package com.project.react_tft.service;

import com.project.react_tft.Repository.MovieRepository;
import com.project.react_tft.domain.Movie;
import com.project.react_tft.dto.MovieDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(MovieDTO movieDTO){

        Optional<Movie> movieData =  movieRepository.findById(movieDTO.getMovie_id());

        Movie movie = modelMapper.map(movieDTO, Movie.class);
        //movie id 존재할 경우
        if (movieData.isPresent()) {
            return movieDTO.getMovie_id();
        } else {
            //movie id 존재하지 않을경우
            return movieRepository.save(movie).getMovie_id();
        }
    }
}

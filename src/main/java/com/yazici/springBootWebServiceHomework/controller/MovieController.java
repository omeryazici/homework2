package com.yazici.springBootWebServiceHomework.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yazici.springBootWebServiceHomework.model.Movie;
import com.yazici.springBootWebServiceHomework.service.IMovieService;
import com.yazici.springBootWebServiceHomework.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MovieController {

    @Autowired
    private IMovieService movieService;

    @RequestMapping(path = "/movies/search", method = RequestMethod.GET)
    public List<Movie> search(@RequestParam(name = "movie_name") String movieName){
        return movieService.search(movieName);
    }

    @PostMapping("/movies/saveToList/{id}")
    public boolean addToList(@PathVariable(name = "id") String id) throws IOException {
        return movieService.addToList(id);
    }

    @PostMapping("/movies/detail/{id}")
    public Movie detail(@PathVariable(name = "id") String id) throws JsonProcessingException {
        return movieService.detail(id);
    }

}

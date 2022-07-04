package com.yazici.springBootWebServiceHomework.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yazici.springBootWebServiceHomework.model.Movie;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;

public interface IMovieService {

    public List<Movie> search(String movieName);

    public boolean addToList(String id) throws IOException;

    public Movie detail(String id) throws JsonProcessingException;

}

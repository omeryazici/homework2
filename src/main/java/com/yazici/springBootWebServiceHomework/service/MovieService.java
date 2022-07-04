package com.yazici.springBootWebServiceHomework.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yazici.springBootWebServiceHomework.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService implements IMovieService {

    @Autowired
    RestTemplate restTemplate;

    private ResponseEntity<String> headers(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        headers.add("authorization", "apikey 66LrGJwlveVZLfOTwlDHOn:1jLjy4FdVFjfDednFLGF3G");
        headers.add("user-agent", "Application");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> stringResponseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return stringResponseEntity;
    }

    private boolean wFile(String line, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileWriter fileWriter = new FileWriter(file, false);
        BufferedWriter bWriter = new BufferedWriter(fileWriter);
        bWriter.write(line);
        bWriter.close();
        return true;
    }

    private String rFile(String id,String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        String line;
        BufferedReader br = new BufferedReader(fileReader);

        while ((line = br.readLine()) != null) {
            if(line.contains(id)){
                break;
            }
        }
        br.close();
        return line;
    }

    @Override
    public List<Movie> search(String movieName) {

        String url = "https://api.collectapi.com/imdb/imdbSearchByName?query=" + movieName;
        ResponseEntity<String> stringResponseEntity = headers(url);
        String r = stringResponseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Movie> movies = new ArrayList<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(r);
            JsonNode resultNode = jsonNode.get("result");
            if (resultNode.isArray()) {
                ArrayNode moviesNode = (ArrayNode) resultNode;
                for (int i = 0; i < moviesNode.size(); i++) {
                    JsonNode singleMovie = moviesNode.get(i);
                    String title = singleMovie.get("Title").toString();
                    String year = singleMovie.get("Year").toString();
                    String imdbId = singleMovie.get("imdbID").toString();
                    String type = singleMovie.get("Type").toString();
                    Movie movie = new Movie();
                    movie.setTitle(title);
                    movie.setYear(year);
                    movie.setImdbID(imdbId);
                    movie.setType(type);
                    movies.add(movie);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    public boolean addToList(String id) throws IOException {
        boolean result = false;
        String url = "https://api.collectapi.com/imdb/imdbSearchById?movieId=" + id;
        ResponseEntity<String> stringResponseEntity = headers(url);
        String r = stringResponseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(r);
        JsonNode resultNode = jsonNode.get("result");
        if (resultNode.isObject()) {
            String title = resultNode.get("Title").toString();
            String year = resultNode.get("Year").toString();
            String imdbId = resultNode.get("imdbID").toString();
            String type = resultNode.get("Type").toString();
            String file = id+": "+title + ", " + year + ", " + imdbId + ", " + type;
            try {
                result = wFile(file, "liste.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Movie detail(String id) throws JsonProcessingException {
        String result = null;
        Movie movie = new Movie();
        try {
            result = rFile(id,"liste.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result != null){
            String[] tokens = result.split(" ");
            movie.setTitle(tokens[1]);
            movie.setYear(tokens[2]);
            movie.setImdbID(tokens[3]);
            movie.setType(tokens[4]);
        }else{
            String url = "https://api.collectapi.com/imdb/imdbSearchById?movieId=" + id;
            ResponseEntity<String> stringResponseEntity = headers(url);
            String r = stringResponseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(r);
            JsonNode resultNode = jsonNode.get("result");
            if (resultNode.isObject()) {
                movie.setTitle(resultNode.get("Title").toString());
                movie.setYear(resultNode.get("Year").toString());
                movie.setImdbID(resultNode.get("imdbID").toString());
                movie.setType(resultNode.get("Type").toString());
            }
        }
        return movie;
    }

}

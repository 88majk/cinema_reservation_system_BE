package com.example.cinemaressys.controllers;

import com.example.cinemaressys.dtos.moviesession.MovieSessionInfoResponse;
import com.example.cinemaressys.exception.MyException;
import com.example.cinemaressys.services.moviesession.MovieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movieSession")
@CrossOrigin(origins = "http://149.100.159.18:8084")
public class MovieSessionController {
    private final MovieSessionService movieSessionService;

    @Autowired
    public MovieSessionController(MovieSessionService movieSessionService){
        this.movieSessionService = movieSessionService;
    }

    @GetMapping("/{movieSessionId}")
    public ResponseEntity<?> getMovieSessionInfo(@PathVariable int movieSessionId){
        try{
            MovieSessionInfoResponse movieSessionInfoResponse = movieSessionService.getMovieSessionInfo(movieSessionId);
            return ResponseEntity.ok().body(movieSessionInfoResponse);
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage()
                    + ". Please try again later.");
        }
    }
}

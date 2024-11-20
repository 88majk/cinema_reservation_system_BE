package com.example.cinemaressys.controllers;

import com.example.cinemaressys.dtos.moviesession.MovieSessionResponse;
import com.example.cinemaressys.exception.MyException;
import com.example.cinemaressys.services.moviesession.MovieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/cinemas/{cinemasId}")
@CrossOrigin(origins = "http://149.100.159.18:8084")
public class CinemasMovieSessionController {
    private final MovieSessionService movieSessionService;
    @Autowired
    public CinemasMovieSessionController(MovieSessionService movieSessionService){
        this.movieSessionService = movieSessionService;
    }
    @GetMapping("/movieSession")
    @ApiOperation("Get movie sessions for a given cinema and date")
    public ResponseEntity<?> getMoviesAndSession(@RequestParam int cinemaId, @RequestParam String movieSessionDate){
        try{
            MovieSessionResponse json = movieSessionService.getMoviesSessions(cinemaId, movieSessionDate);
            return ResponseEntity.ok().body(json);
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage()
                    + ". Please try again later.");
        }
    }

}

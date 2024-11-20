package com.example.cinemaressys.controllers;

import com.example.cinemaressys.dtos.movie.MovieRequestDto;
import com.example.cinemaressys.exception.MyException;
import com.example.cinemaressys.services.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie")
@CrossOrigin(origins = "http://149.100.159.18:8084")
public class MovieController {
    private final MovieService movieService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody MovieRequestDto requestDto){
        try {
            movieService.addMovie(requestDto);
            return ResponseEntity.ok().body("Movie " + requestDto.getName() + " was successfully added.");
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e)  {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage() +
                    ". Please try again later.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        try {
            //movieService.getMoviesFromApi();
            return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage() +
                    ". Please try again later.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id){
        try{
            return ResponseEntity.ok()
                    .body(Objects.requireNonNullElseGet(movieService.getMovie(id),
                            () -> "Movie with id " + id + " not found."));
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage() +
                    ". Please try again later.");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            if(movieService.deleteMovie(id)) {
                return ResponseEntity.ok().body( "Movie with id " + id + " was successfully deleted.");
            } else
                return ResponseEntity.ok().body("Movie with id " + id + " not found.");
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage() +
                    ". Please try again later.");
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody MovieRequestDto requestDto){
        try {
            movieService.updateMovie(id, requestDto);
            return ResponseEntity.ok().body("Movie " + id + " was updated.");
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: "+ e.getMessage() +
                    ". Please try again later.");
        }
    }
}

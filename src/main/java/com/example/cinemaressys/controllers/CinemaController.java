package com.example.cinemaressys.controllers;

import com.example.cinemaressys.dtos.cinema.CinemaRequestDto;
import com.example.cinemaressys.exception.MyException;
import com.example.cinemaressys.services.cinema.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cinema")
@CrossOrigin(origins = "http://149.100.159.18:8084")
public class CinemaController {
    private final CinemaService cinemaService;
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CinemaRequestDto requestDto) {
        try{
            cinemaService.addCinema(requestDto);
            return ResponseEntity.ok("Cinema " + requestDto.getName() + " was successfully added.");
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage() +
                    ". Please try again later.");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            return new ResponseEntity<>(cinemaService.getAllCinemas(), HttpStatus.OK);
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage()
                    + ". Please try again later.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id){
        try{
            return ResponseEntity.ok()
                    .body(Objects.requireNonNullElseGet(cinemaService.getCinema(id),
                            () -> "Cinema with id " + id + " not found."));
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage()
                    + ". Please try again later.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try{
            if(cinemaService.deleteCinema(id))
                return ResponseEntity.ok("Cinema with id " + id + " was successfully deleted.");
            else
                return ResponseEntity.ok().body("Cinema with id " + id + " not found.");
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage() +
                    ". Please try again later.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody CinemaRequestDto requestDto){
        try {
            cinemaService.updateCinema(id, requestDto);
            return ResponseEntity.ok("Cinema with id " + id + " was successfully updated.");
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage() +
                    "Please try again later.");
        }
    }
}

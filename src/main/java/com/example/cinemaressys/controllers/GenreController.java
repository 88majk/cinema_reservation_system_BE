package com.example.cinemaressys.controllers;

import com.example.cinemaressys.dtos.genre.GenreResponseDto;
import com.example.cinemaressys.services.genre.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/genre")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://149.100.159.18:8084")
public class GenreController {
    private final GenreService genreService;
    @GetMapping("/all")
    public List<GenreResponseDto> getAll(){
        return genreService.getAllGenres();
    }
}

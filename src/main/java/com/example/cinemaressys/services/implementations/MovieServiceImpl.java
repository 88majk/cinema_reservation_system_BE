package com.example.cinemaressys.services.implementations;

import com.example.cinemaressys.dtos.movie.MovieRequestDto;
import com.example.cinemaressys.dtos.movie.MovieResponseDto;
import com.example.cinemaressys.entities.Genre;
import com.example.cinemaressys.entities.Movie;
import com.example.cinemaressys.repositories.MovieRepositories;
import com.example.cinemaressys.services.MovieService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepositories movieRepository;

    @Override
    public void addMovie(MovieRequestDto requestDto) {
        movieRepository.save(new Movie(
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getReleaseDate(),
                requestDto.getMinimumAge(),
                requestDto.getDuration(),
                requestDto.getProductionCountry(),
                requestDto.getDirector(),
                requestDto.getMovieGenres()
        ));
    }

    @Override
    public List<MovieResponseDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movie -> {
                    Set<Genre> movieGenres = movieRepository.findGenresByMovieId(movie.getMovieId());
                    return new MovieResponseDto(
                            movie.getMovieId(),
                            movie.getName(),
                            movie.getDescription(),
                            movie.getReleaseDate(),
                            movie.getMinimumAge(),
                            movie.getDuration(),
                            movie.getProductionCountry(),
                            movie.getDirector(),
                            movieGenres
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public MovieResponseDto getMovie(int movieId) {
        Optional<Movie> movieById = movieRepository.findById(movieId);
        return movieById.map(
                movie -> {
                    Set<Genre> movieGenres = movieRepository.findGenresByMovieId(movieId);
                    return new MovieResponseDto(
                            movie.getMovieId(),
                            movie.getName(),
                            movie.getDescription(),
                            movie.getReleaseDate(),
                            movie.getMinimumAge(),
                            movie.getDuration(),
                            movie.getProductionCountry(),
                            movie.getDirector(),
                            movieGenres
                    );
                }
        ).orElse(null);
    }

    @Override //PROBLEM Z UPDATEM (PRZEZ GENRE)
    public void updateMovie(int movieId, MovieRequestDto requestDto) {
        Movie existingMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie with id " + movieId + " not found!"));

        existingMovie.setName(requestDto.getName());
        existingMovie.setDescription(requestDto.getDescription());
        existingMovie.setReleaseDate(requestDto.getReleaseDate());
        existingMovie.setMinimumAge(requestDto.getMinimumAge());
        existingMovie.setDuration(requestDto.getDuration());
        existingMovie.setProductionCountry(requestDto.getProductionCountry());
        existingMovie.setDirector(requestDto.getDirector());
        existingMovie.setMovieGenres(requestDto.getMovieGenres());

        movieRepository.save(existingMovie);
    }

    @Override
    public Boolean deleteMovie(int movieId) {
        if (movieRepository.existsById(movieId)) {
            movieRepository.deleteById(movieId);
            return true;
        } else
            return false;
    }
}
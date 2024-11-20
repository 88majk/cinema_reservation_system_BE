package com.example.cinemaressys.controllers;

import com.example.cinemaressys.dtos.access.AccessCreateAdminRequestDto;
import com.example.cinemaressys.dtos.seat.GetSeatRequestDto;
import com.example.cinemaressys.dtos.seat.ListSeatRequestDto;
import com.example.cinemaressys.dtos.seat.SeatsResponseDto;
import com.example.cinemaressys.exception.MyException;
import com.example.cinemaressys.services.access.AccessService;
import com.example.cinemaressys.services.seat.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/seats")
@CrossOrigin(origins = "http://149.100.159.18:8084")
public class SeatController {
    private final SeatService seatService;

    @Autowired
    public SeatController(SeatService seatService){
        this.seatService = seatService;
    }
    @PostMapping("/seatsNewHall")
    public ResponseEntity<?> createSeats(@RequestBody ListSeatRequestDto listSeatRequestDto) {
        try {
            seatService.createSeatsInNewHall(listSeatRequestDto);
            return ResponseEntity.ok().body("New admin has been added!");
        } catch (MyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/movieSession/{movieSessionId}")
    public ResponseEntity<?> getSeatsByMovieSessionId(@PathVariable int movieSessionId,
                                                      @RequestBody GetSeatRequestDto getSeatRequestDto){
        try{
            SeatsResponseDto seatsResponseDto = seatService.getSeatsByMovieSessionId(movieSessionId,
                    getSeatRequestDto.getBookingId());
            return ResponseEntity.ok().body(seatsResponseDto);
        } catch (MyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error: " + e.getMessage()
                    + ". Please try again later.");
        }
    }
}

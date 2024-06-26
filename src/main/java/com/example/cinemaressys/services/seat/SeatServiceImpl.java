package com.example.cinemaressys.services.seat;

import com.example.cinemaressys.dtos.seat.ListSeatRequestDto;
import com.example.cinemaressys.dtos.seat.RowDto;
import com.example.cinemaressys.dtos.seat.SeatDto;
import com.example.cinemaressys.dtos.seat.SeatsResponseDto;
import com.example.cinemaressys.entities.*;
import com.example.cinemaressys.exception.MyException;
import com.example.cinemaressys.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class SeatServiceImpl implements SeatService{
    final private CinemaHallRepositories cinemaHallRepositories;
    final private SeatRepositories seatRepositories;
    final private DictSeatClassRepositories dictSeatClassRepositories;
    final private MovieSessionRepositories movieSessionRepositories;
    final private BookingSeatRepositories bookingSeatRepositories;
    final private PriceRepositories priceRepositories;
    final private UserRepositories userRepositories;
    final private DictBookingStatusRepositories dictBookingStatusRepositories;
    final private BookingRepositories bookingRepositories;

    @Override
    public void createSeatsInNewHall(ListSeatRequestDto listSeatRequestDto) {
        if (cinemaHallRepositories.getCinemaHallByCinemaHallId(listSeatRequestDto.getCinemaHallId()) == null){
            throw new MyException("The cinema room with the given ID does not exist.");
        }
        CinemaHall cinemaHall = cinemaHallRepositories.getCinemaHallByCinemaHallId(listSeatRequestDto.getCinemaHallId());
        List<Seat> seatCinemaHall = seatRepositories.findByCinemaHallCinemaHallId(listSeatRequestDto.getCinemaHallId());
        if (!seatCinemaHall.isEmpty()){
            throw new MyException("This cinema room already has assigned seats. " + seatCinemaHall.get(0).getSeatId());
        }
        else{
            for (SeatDto seatDto : listSeatRequestDto.getSeats()){
                DictSeatClass dictSeatClass = dictSeatClassRepositories.findByName(seatDto.getSeatClass());
                if (dictSeatClass == null){
                    throw new MyException("This class does not exist in cinema.");
                }
//                for (int i=1;i<=seatDto.getColumns();i++){
//                    Seat seat = new Seat(
//                            cinemaHall,
//                            dictSeatClass,
//                            seatDto.get(),
//                            i
//                    );
//                    seatRepositories.save(seat);
//                }

            }
        }

    }

    @Override
    public SeatsResponseDto getSeatsByMovieSessionId(int movieSessionId, int bookingId) {
        MovieSession movieSession = movieSessionRepositories.findByMovieSessionId(movieSessionId);
        if(movieSession == null){
            throw new MyException("movieSession with Id: " + movieSessionId + " not found");
        }
        List<Seat> seatList = seatRepositories.findByCinemaHallCinemaHallId(
                movieSession.getCinemaHall().getCinemaHallId());
        if (seatList.isEmpty()){
            throw new MyException("Seats in cinema hall are not found.");
        }
        List<Price> priceList = priceRepositories.findByMovieSessionMovieSessionId(movieSessionId);
        if (priceList.isEmpty()){
            throw new MyException("Price for seats are not found.");
        }
        Map<Integer, Float> priceMap = new HashMap<>();
        for (Price price : priceList) {
            priceMap.put(price.getDictSeatClass().getDictSeatClassId(), price.getPrice());
        }

        List<Integer> statusIds = Arrays.asList(1, 2);
        List<BookingSeat> bookedSeatsList = bookingSeatRepositories.
                getByMovieSessionMovieSessionIdAndDictBookingStatusIdIn(movieSessionId, statusIds);

        List<BookingSeat> seatsInBooking = new ArrayList<>();
        if (bookingId > 0) {
            seatsInBooking = bookingSeatRepositories.
                    findByBookingBookingIdAndMovieSessionMovieSessionId(bookingId, movieSessionId);
        }

        Map<Character, List<SeatDto>> rowMap = new HashMap<>();
        List<SeatDto> seatDtoList = new ArrayList<>();

        for (Seat seat : seatList){
            Character rowName = seat.getRowNumber();

            if (!rowMap.containsKey(rowName)) {
                // Jeśli nie istnieje, utwórz nowy obiekt RowDto i dodaj go do mapy
                rowMap.put(rowName, new ArrayList<>());
            }
            List<SeatDto> seatDtosForRow = rowMap.get(rowName);

            SeatDto seatDto = new SeatDto();
            seatDto.setSeatId(seat.getSeatId());
            seatDto.setSeatClass(seat.getDictSeatClass().getName());
            seatDto.setColumn(seat.getColumnNumber());

            if (priceMap.containsKey(seat.getDictSeatClass().getDictSeatClassId())) {
                seatDto.setPrice(priceMap.get(seat.getDictSeatClass().getDictSeatClassId()));
            } else {
                seatDto.setPrice(0);
            }
            BookingSeat foundBookingSeat = null;
            for (BookingSeat bookingSeat : bookedSeatsList) {
                if (bookingSeat.getSeat().getSeatId() == seatDto.getSeatId()) {
                    foundBookingSeat = bookingSeat;
                    break;
                }
            }
            if (foundBookingSeat != null){
                seatDto.setAvailable(false);
                seatDto.setBookingStatus(foundBookingSeat.getDictBookingStatus().getName());
            }
            else{
                seatDto.setAvailable(true);
                seatDto.setBookingStatus(null);
            }
            if (!seatsInBooking.isEmpty()) {
                BookingSeat bookingSeatInBooking = null;
                for (BookingSeat bookingSeat: seatsInBooking) {
                    if (bookingSeat.getSeat().getSeatId() == seatDto.getSeatId()) {
                        bookingSeatInBooking = bookingSeat;
                        break;
                    }
                }
                if (bookingSeatInBooking != null){
                    seatDto.setInBooking(true);
                    seatDto.setAvailable(true);
                }
                else{
                    seatDto.setInBooking(false);
                }
            }
            seatDto.setRow(seat.getRowNumber());
            seatDtosForRow.add(seatDto);
        }

        // Iteruj po mapie i utwórz obiekty RowDto
        List<RowDto> rowDtoList = new ArrayList<>();
        for (Map.Entry<Character, List<SeatDto>> entry : rowMap.entrySet()) {
            RowDto rowDto = new RowDto();

            List<SeatDto> sortedSeatDtoList = entry.getValue();
            sortedSeatDtoList.sort(Comparator.comparingInt(SeatDto::getSeatId));

            rowDto.setRowName(entry.getKey().toString()); // Konwersja Character na String
            rowDto.setSeats(sortedSeatDtoList);
            rowDtoList.add(rowDto);
        }

        SeatsResponseDto seatsResponseDto = new SeatsResponseDto(
                movieSession.getCinemaHall().getCinemaHallId(),
                movieSession.getCinemaHall().getName(),
                rowDtoList
        );

        return seatsResponseDto;
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.InvalidStatusException;
import ru.practicum.shareit.common.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingGateway {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid BookingDto bookingDto) {
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        return bookingClient.get(userId, bookingId);
    }

    //pagination
    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(name = "state", defaultValue = "ALL") String value,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10") Long size)
            throws InvalidStatusException {
        State state = validateState(value);
        return bookingClient.get(userId, state, from, size);
    }

    //pagination
    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String value,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Long size)
            throws InvalidStatusException {
        State state = validateState(value);
        return bookingClient.getByOwner(userId, state, from, size);
    }

    private State validateState(String value) throws InvalidStatusException {
        State state = State.ALL;
        try {
            state = State.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Unknown state: " + value);
        }
        return state;
    }

}

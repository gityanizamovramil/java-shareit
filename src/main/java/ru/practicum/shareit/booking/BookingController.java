package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidDateTimeException;
import ru.practicum.shareit.booking.exception.InvalidStatusException;
import ru.practicum.shareit.booking.exception.NotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.Valid;
import java.util.List;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingInfoDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto
    ) throws UserNotFoundException, ItemNotFoundException, InvalidDateTimeException, NotAvailableException {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto approve(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) throws UserNotFoundException, BookingNotFoundException, InvalidStatusException {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId
    ) throws UserNotFoundException, BookingNotFoundException {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingInfoDto> get(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state
    ) throws UserNotFoundException, InvalidStatusException {
        return bookingService.get(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state
    ) throws UserNotFoundException, InvalidStatusException {
        return bookingService.getByOwner(userId, state);
    }

}

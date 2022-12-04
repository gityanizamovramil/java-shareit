package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidDateTimeException;
import ru.practicum.shareit.booking.exception.InvalidStatusException;
import ru.practicum.shareit.booking.exception.NotAvailableException;
import ru.practicum.shareit.common.PaginationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingInfoDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingDto bookingDto
    ) throws UserNotFoundException, ItemNotFoundException, InvalidDateTimeException, NotAvailableException {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
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

    //pagination
    @GetMapping
    public List<BookingInfoDto> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam String state,
                                    @RequestParam Long from,
                                    @RequestParam Long size
    ) throws UserNotFoundException, InvalidStatusException, PaginationException {
        return bookingService.get(userId, state, from, size);
    }

    //pagination
    @GetMapping("/owner")
    public List<BookingInfoDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam String state,
                                           @RequestParam Long from,
                                           @RequestParam Long size
    ) throws UserNotFoundException, InvalidStatusException, PaginationException {
        return bookingService.getByOwner(userId, state, from, size);
    }

}

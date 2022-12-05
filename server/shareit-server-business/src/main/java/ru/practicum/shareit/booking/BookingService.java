package ru.practicum.shareit.booking;

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

public interface BookingService {

    BookingInfoDto create(Long userId, BookingDto bookingDto)
            throws UserNotFoundException, ItemNotFoundException, NotAvailableException, InvalidDateTimeException;

    BookingInfoDto approve(Long userId, Long bookingId, Boolean approved)
            throws BookingNotFoundException, UserNotFoundException, InvalidStatusException;

    BookingInfoDto get(Long userId, Long bookingId)
            throws BookingNotFoundException, UserNotFoundException;

    List<BookingInfoDto> get(Long userId, String state, Long from, Long size)
            throws UserNotFoundException, InvalidStatusException, PaginationException;

    List<BookingInfoDto> getByOwner(Long userId, String state, Long from, Long size)
            throws UserNotFoundException, InvalidStatusException, PaginationException;
}

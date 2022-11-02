package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(
            UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingInfoDto create(Long userId, BookingDto bookingDto)
            throws UserNotFoundException, ItemNotFoundException, NotAvailableException, InvalidDateTimeException {
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("item not found"));
        if (!item.getAvailable()) throw new NotAvailableException("item is not available");
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) throw new InvalidDateTimeException("time is wrong");

        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        if (booker.getId().equals(item.getOwner().getId())) throw new UserNotFoundException("user not found");

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public BookingInfoDto approve(Long userId, Long bookingId, Boolean approved)
            throws BookingNotFoundException, UserNotFoundException, InvalidStatusException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("booking not found"));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId())) throw new UserNotFoundException("user not found");
        if (booking.getStatus().equals(Status.APPROVED) ||
                booking.getStatus().equals(Status.REJECTED)) throw new InvalidStatusException("no change allowed");
        if (approved != null) booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public BookingInfoDto get(Long userId, Long bookingId) throws BookingNotFoundException, UserNotFoundException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("booking not found"));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new UserNotFoundException("user not found");
        }
        return BookingMapper.toBookingInfoDto(booking);
    }

    //pagination
    @Override()
    public List<BookingInfoDto> get(Long userId, String value, Long from, Long size)
            throws UserNotFoundException, InvalidStatusException, PaginationException {
        State state = validateState(value);
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        if (from < 0) throw new PaginationException("paging invalid");
        if (size <= 0) throw new PaginationException("paging invalid");
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue());
        switch (state) {
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), sort, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), sort, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                userId, LocalDateTime.now(), LocalDateTime.now(), sort, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, Status.REJECTED, pageRequest);
                break;
            default:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, pageRequest);
                break;
        }

        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
    }

    //pagination
    @Override
    public List<BookingInfoDto> getByOwner(Long userId, String value, Long from, Long size)
            throws UserNotFoundException, InvalidStatusException, PaginationException {
        State state = validateState(value);
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        if (from < 0) throw new PaginationException("paging invalid");
        if (size <= 0) throw new PaginationException("paging invalid");
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue());
        switch (state) {
            case PAST:
                bookings = bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sort, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), sort, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                                userId, LocalDateTime.now(), LocalDateTime.now(), sort, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(userId, Status.REJECTED, pageRequest);
                break;
            default:
                bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId, pageRequest);
                break;
        }

        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
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

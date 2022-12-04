package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.common.State;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidDateTimeException;
import ru.practicum.shareit.booking.exception.InvalidStatusException;
import ru.practicum.shareit.booking.exception.NotAvailableException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.Status;
import ru.practicum.shareit.booking.strategy.BookingStateFetchStrategy;
import ru.practicum.shareit.booking.strategy.StrategyFactoryForBooker;
import ru.practicum.shareit.booking.strategy.StrategyFactoryForOwner;
import ru.practicum.shareit.booking.strategy.StrategyName;
import ru.practicum.shareit.common.PaginationException;
import ru.practicum.shareit.utils.PageRequestManager;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

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

    @Autowired
    private final StrategyFactoryForOwner strategyFactoryForOwner;

    @Autowired
    private final StrategyFactoryForBooker strategyFactoryForBooker;

    public BookingServiceImpl(
            UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository,
            StrategyFactoryForOwner strategyFactoryForOwner, StrategyFactoryForBooker strategyFactoryForBooker) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.strategyFactoryForOwner = strategyFactoryForOwner;
        this.strategyFactoryForBooker = strategyFactoryForBooker;
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
        StrategyName strategyName = StrategyName.valueOf(state.name());
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        List<Booking> bookings = new ArrayList<>();
        PageRequest pageReq = PageRequestManager.form(
                from.intValue(), size.intValue(), Sort.Direction.DESC, "start");
        BookingStateFetchStrategy strategyForBooker = strategyFactoryForBooker.findStrategy(strategyName);
        bookings = strategyForBooker.fetch(userId, pageReq);
        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
    }

    //pagination
    @Override
    public List<BookingInfoDto> getByOwner(Long userId, String value, Long from, Long size)
            throws UserNotFoundException, InvalidStatusException, PaginationException {
        State state = validateState(value);
        StrategyName strategyName = StrategyName.valueOf(state.name());
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        List<Booking> bookings = new ArrayList<>();
        PageRequest pageReq = PageRequestManager.form(
                from.intValue(), size.intValue(), Sort.Direction.DESC, "start");
        BookingStateFetchStrategy strategyForOwner = strategyFactoryForOwner.findStrategy(strategyName);
        bookings = strategyForOwner.fetch(userId, pageReq);
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

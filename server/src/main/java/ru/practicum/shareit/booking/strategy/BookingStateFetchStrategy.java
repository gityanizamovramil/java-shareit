package ru.practicum.shareit.booking.strategy;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingStateFetchStrategy {

    List<Booking> fetch(Long userId, PageRequest pageRequest);

    StrategyName getStrategy();
}

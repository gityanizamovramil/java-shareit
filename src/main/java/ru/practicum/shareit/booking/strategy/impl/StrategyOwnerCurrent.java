package ru.practicum.shareit.booking.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.strategy.BookingStateFetchStrategyForOwner;
import ru.practicum.shareit.booking.strategy.StrategyName;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StrategyOwnerCurrent implements BookingStateFetchStrategyForOwner {

    @Autowired
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> fetch(Long userId, PageRequest pageRequest) {
        return bookingRepository
                .findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
    }

    @Override
    public StrategyName getStrategy() {
        return StrategyName.CURRENT;
    }
}

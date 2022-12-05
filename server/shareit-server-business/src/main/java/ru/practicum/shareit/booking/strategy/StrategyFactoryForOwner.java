package ru.practicum.shareit.booking.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.strategy.impl.*;

import java.util.HashMap;
import java.util.Map;

@Component
public class StrategyFactoryForOwner {

    private final Map<StrategyName, BookingStateFetchStrategy> strategies = new HashMap<>();

    @Autowired
    private final BookingRepository bookingRepository;

    public StrategyFactoryForOwner(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        createStrategy();
    }

    public BookingStateFetchStrategy findStrategy(StrategyName strategyName) {
        return strategies.get(strategyName);
    }

    private void createStrategy() {
        strategies.put(StrategyName.ALL, new StrategyOwnerAll(bookingRepository));
        strategies.put(StrategyName.CURRENT, new StrategyOwnerCurrent(bookingRepository));
        strategies.put(StrategyName.FUTURE, new StrategyOwnerFuture(bookingRepository));
        strategies.put(StrategyName.PAST, new StrategyOwnerPast(bookingRepository));
        strategies.put(StrategyName.REJECTED, new StrategyOwnerRejected(bookingRepository));
        strategies.put(StrategyName.WAITING, new StrategyOwnerWaiting(bookingRepository));
    }
}

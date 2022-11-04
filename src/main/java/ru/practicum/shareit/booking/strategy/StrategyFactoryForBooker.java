package ru.practicum.shareit.booking.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.strategy.impl.*;

import java.util.HashMap;
import java.util.Map;

@Component
public class StrategyFactoryForBooker {
    private final Map<StrategyName, BookingStateFetchStrategy> strategies = new HashMap<>();

    @Autowired
    private final BookingRepository bookingRepository;

    public StrategyFactoryForBooker(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        createStrategy();
    }

    public BookingStateFetchStrategy findStrategy(StrategyName strategyName) {
        return strategies.get(strategyName);
    }

    private void createStrategy() {
        strategies.put(StrategyName.ALL, new StrategyBookerAll(bookingRepository));
        strategies.put(StrategyName.CURRENT, new StrategyBookerCurrent(bookingRepository));
        strategies.put(StrategyName.FUTURE, new StrategyBookerFuture(bookingRepository));
        strategies.put(StrategyName.PAST, new StrategyBookerPast(bookingRepository));
        strategies.put(StrategyName.REJECTED, new StrategyBookerRejected(bookingRepository));
        strategies.put(StrategyName.WAITING, new StrategyBookerWaiting(bookingRepository));
    }
}

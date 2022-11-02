package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, PageRequest pageRequest); //all by booker id

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort, PageRequest pageRequest); //future by booker id

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long userId, LocalDateTime start, LocalDateTime end, Sort sort, PageRequest pageRequest); //current by booker id

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort, PageRequest pageRequest); //past by booker id

    List<Booking> findAllByBooker_IdAndStatus(Long userId, Status status, PageRequest pageRequest); //by status and booker id

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId, PageRequest pageRequest); // all by owner id

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(
            Long userId, LocalDateTime start, Sort sort, PageRequest pageRequest); // future by owner id

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            Long userId, LocalDateTime start, LocalDateTime end, Sort sort, PageRequest pageRequest); //current by owner id

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort, PageRequest pageRequest); //past by owner id

    List<Booking> findAllByItem_Owner_IdAndStatus(Long userId, Status status, PageRequest pageRequest); //by status and owner id

    Optional<Booking> findTop1BookingByItem_IdAndEndIsBeforeAndStatusIs(
            Long itemId, LocalDateTime end, Status status, Sort sort);

    Optional<Booking> findTop1BookingByItem_IdAndEndIsAfterAndStatusIs(
            Long itemId, LocalDateTime end, Status status, Sort sort);

    Optional<Booking> findTop1BookingByItem_IdAndBooker_IdAndEndIsBeforeAndStatusIs(
            Long itemId, Long bookerId, LocalDateTime end, Status status, Sort sort);

}

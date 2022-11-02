package ru.practicum.shareit.requests;

import ru.practicum.shareit.booking.exception.PaginationException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) throws UserNotFoundException;

    List<ItemRequestDto> get(Long userId) throws UserNotFoundException;

    List<ItemRequestDto> get(Long userId, Long from, Long size) throws UserNotFoundException, PaginationException;

    ItemRequestDto get(Long userId, Long requestId) throws UserNotFoundException, ItemRequestNotFoundException;

}

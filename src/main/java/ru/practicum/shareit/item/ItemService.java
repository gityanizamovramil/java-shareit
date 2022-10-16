package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.InvalidCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto) throws UserNotFoundException;

    ItemDto update(Long userId, Long itemId, ItemDto itemDto) throws ItemNotFoundException, UserNotFoundException;

    ItemDto get(Long userId, Long itemId) throws ItemNotFoundException, UserNotFoundException;

    List<ItemDto> get(Long userId) throws UserNotFoundException;

    List<ItemDto> search(Long userId, String text) throws UserNotFoundException;

    CommentDto comment(Long userId, Long itemId, CommentDto commentDto) throws ItemNotFoundException, UserNotFoundException, InvalidCommentException;
}

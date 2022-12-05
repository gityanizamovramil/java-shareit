package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.PaginationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.InvalidCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto
    ) throws UserNotFoundException {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto
    ) throws ItemNotFoundException, UserNotFoundException {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemId
    ) throws ItemNotFoundException, UserNotFoundException {
        return itemService.get(userId, itemId);
    }

    //pagination
    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam Long from,
                             @RequestParam Long size
    ) throws UserNotFoundException, PaginationException {
        return itemService.get(userId, from, size);
    }

    //pagination
    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam String text,
                                @RequestParam Long from,
                                @RequestParam Long size
    ) throws UserNotFoundException, PaginationException {
        return itemService.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody CommentDto commentDto
    ) throws UserNotFoundException, InvalidCommentException, ItemNotFoundException {
        return itemService.comment(userId, itemId, commentDto);
    }

}

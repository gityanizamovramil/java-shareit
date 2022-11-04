package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.exception.PaginationException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @Autowired
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto
    ) throws UserNotFoundException {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) throws UserNotFoundException {
        return itemRequestService.get(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> get(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Long from,
            @RequestParam(defaultValue = "10") Long size
    ) throws UserNotFoundException, PaginationException {
        return itemRequestService.get(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(
            @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId
    ) throws UserNotFoundException, ItemRequestNotFoundException {
        return itemRequestService.get(userId, requestId);
    }

}

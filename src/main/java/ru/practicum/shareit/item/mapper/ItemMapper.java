package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;

import java.util.Collections;

public class ItemMapper {

    public static ItemInfoDto toItemInfoDto(Item item) {
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .request(null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest() == null ? null : ItemRequest.builder().id(itemDto.getRequest()).build())
                .build();
    }

    public static Item matchItem(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .available(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable())
                .request(itemDto.getRequest() == null ?
                        item.getRequest() : ItemRequest.builder().id(itemDto.getRequest()).build())
                .build();
    }
}

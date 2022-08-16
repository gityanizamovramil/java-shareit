package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) throws UserNotFoundException {
        User owner = userRepository.get(userId);
        if (owner == null) throw new UserNotFoundException("user not found");
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        itemRepository.save(userId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto)
            throws ItemNotFoundException, UserNotFoundException {
        User owner = userRepository.get(userId);
        if (owner == null) throw new UserNotFoundException("user not found");
        Item repoItem = itemRepository.get(userId, itemId);
        if (repoItem == null) throw new ItemNotFoundException("item not found");
        itemDto.setId(itemId);
        Item item = ItemMapper.matchItem(itemDto, repoItem);
        item.setOwner(owner);
        itemRepository.save(userId, item);
        item = itemRepository.get(userId, itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto get(Long userId, Long itemId) throws ItemNotFoundException, UserNotFoundException {
        final User user = userRepository.get(userId);
        if (user == null) throw new UserNotFoundException("user not found");
        Item repoItem = itemRepository.find(itemId);
        User owner = repoItem.getOwner();
        ItemDto itemDto = ItemMapper.toItemDto(repoItem);
        itemDto.setOwner(owner.getId());
        return itemDto;
    }

    @Override
    public List<ItemDto> get(Long userId) throws UserNotFoundException {
        final User owner = userRepository.get(userId);
        if (owner == null) throw new UserNotFoundException("user not found");
        List<Item> repoItems = itemRepository.get(userId);
        if (repoItems.isEmpty()) return new ArrayList<>();
        return repoItems.stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> itemDto.setOwner(owner.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Long userId, String text) throws UserNotFoundException {
        final User repoUser = userRepository.get(userId);
        if (repoUser == null) throw new UserNotFoundException("user not found");
        if (text.isEmpty()) return Collections.emptyList();
        List<Item> searchItems = itemRepository.search(text);
        List<ItemDto> searchItemDto = new ArrayList<>();
        for (Item item : searchItems) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setOwner(item.getOwner().getId());
            searchItemDto.add(itemDto);
        }
        return searchItemDto.isEmpty() ? Collections.emptyList() : searchItemDto;
    }

}

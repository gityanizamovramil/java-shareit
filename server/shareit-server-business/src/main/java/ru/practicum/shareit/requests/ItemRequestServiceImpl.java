package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.PaginationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PageRequestManager;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository
    ) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    //получить список своих запросов
    @Override
    public List<ItemRequestDto> get(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);
        if (itemRequests.isEmpty()) return Collections.emptyList();
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        List<Long> requestIdList = itemRequestDtos.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequest_IdIn(requestIdList);

        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                    .collect(Collectors.toList());
            if (!requestItems.isEmpty()) {
                List<ItemDto> itemDtos = requestItems.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
                itemRequestDto.setItems(itemDtos);
            }
        }
        return itemRequestDtos;
    }

    //список запросов созданных другими пользователями
    //от наиболее новых к наиболее старым
    //результаты постранично
    //from - индекс первого элемента
    //size - количество элементов для отображения
    @Override
    public List<ItemRequestDto> get(Long userId, Long from, Long size) throws
            UserNotFoundException, PaginationException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));

        PageRequest pageRequest = PageRequestManager.form(
                from.intValue(), size.intValue(), Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdIsNot(userId, pageRequest);

        List<ItemRequestDto> itemRequestDtos = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        List<Long> requestIdList = itemRequestDtos.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequest_IdIn(requestIdList);

        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                    .collect(Collectors.toList());
            if (!requestItems.isEmpty()) {
                List<ItemDto> itemDtos = requestItems.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
                itemRequestDto.setItems(itemDtos);
            }
        }
        return itemRequestDtos;
    }

    //получить запрос по айди (любой пользователь любой запрос)
    @Override
    public ItemRequestDto get(Long userId, Long requestId) throws
            UserNotFoundException, ItemRequestNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        ItemRequest itemRequest = itemRequestRepository.findItemRequestById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("request not found"));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        List<Item> items = itemRepository.findAllByRequest_Id(itemRequestDto.getId());
        if (!items.isEmpty()) {
            List<ItemDto> itemDtos = items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            itemRequestDto.setItems(itemDtos);
        }
        return itemRequestDto;
    }
}

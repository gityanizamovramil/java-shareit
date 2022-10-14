package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.InvalidCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final CommentRepository commentRepository;

    public ItemServiceImpl(
            UserRepository userRepository,
            ItemRepository itemRepository,
            BookingRepository bookingRepository,
            CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) throws UserNotFoundException {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto)
            throws ItemNotFoundException, UserNotFoundException {

        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        Item repoItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("item not found"));
        if (!repoItem.getOwner().getId().equals(owner.getId())) throw new ItemNotFoundException("item not found");

        itemDto.setId(itemId);
        Item item = ItemMapper.matchItem(itemDto, repoItem);
        item.setOwner(owner);

        itemRepository.save(item);
        item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("item not found"));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto get(Long userId, Long itemId) throws ItemNotFoundException, UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        Item repoItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("item not found"));
        User owner = repoItem.getOwner();

        ItemDto itemDto = ItemMapper.toItemDto(repoItem);
        itemDto.setOwner(owner.getId());

        List<Comment> commentList = commentRepository.findAllByItem_Id(itemId);
        List<CommentDto> commentDtos = commentList.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);

        if (!user.getId().equals(owner.getId())) return itemDto;

        Sort sortDesc = Sort.by(Sort.Direction.DESC, "end");
        Optional<Booking> lastBooking = bookingRepository.findTop1BookingByItem_IdAndEndIsBeforeAndStatusIs(
                itemId, LocalDateTime.now(), Status.APPROVED, sortDesc);

        itemDto.setLastBooking(lastBooking.isEmpty() ? null : LastBookingDto.builder()
                .id(lastBooking.get().getId())
                .bookerId(lastBooking.get().getBooker().getId())
                .start(lastBooking.get().getStart())
                .end(lastBooking.get().getEnd())
                .build());

        Sort sortAsc = Sort.by(Sort.Direction.ASC, "end");
        Optional<Booking> nextBooking = bookingRepository.findTop1BookingByItem_IdAndEndIsAfterAndStatusIs(
                itemId, LocalDateTime.now(), Status.APPROVED, sortAsc);

        itemDto.setNextBooking(nextBooking.isEmpty() ? null : NextBookingDto.builder()
                .id(nextBooking.get().getId())
                .bookerId(nextBooking.get().getBooker().getId())
                .start(nextBooking.get().getStart())
                .end(nextBooking.get().getEnd())
                .build());

        return itemDto;
    }

    @Override
    public List<ItemDto> get(Long userId) throws UserNotFoundException {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        List<Item> repoItems = itemRepository.findAllByOwner_Id(userId);
        if (repoItems.isEmpty()) return new ArrayList<>();

        List<ItemDto> itemDtoList = repoItems.stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> itemDto.setOwner(owner.getId()))
                .collect(Collectors.toList());

        for (ItemDto itemDto : itemDtoList) {

            List<Comment> commentList = commentRepository.findAllByItem_Id(itemDto.getId());
            List<CommentDto> commentDtos = commentList.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
            itemDto.setComments(commentDtos);

            Sort sortDesc = Sort.by(Sort.Direction.DESC, "end");
            Optional<Booking> lastBooking = bookingRepository.findTop1BookingByItem_IdAndEndIsBeforeAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), Status.APPROVED, sortDesc);

            itemDto.setLastBooking(lastBooking.isEmpty() ? LastBookingDto.builder().build() : LastBookingDto.builder()
                    .id(lastBooking.get().getId())
                    .bookerId(lastBooking.get().getBooker().getId())
                    .start(lastBooking.get().getStart())
                    .end(lastBooking.get().getEnd())
                    .build());

            Sort sortAsc = Sort.by(Sort.Direction.ASC, "end");
            Optional<Booking> nextBooking = bookingRepository.findTop1BookingByItem_IdAndEndIsAfterAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), Status.APPROVED, sortAsc);

            itemDto.setNextBooking(nextBooking.isEmpty() ? NextBookingDto.builder().build() : NextBookingDto.builder()
                    .id(nextBooking.get().getId())
                    .bookerId(nextBooking.get().getBooker().getId())
                    .start(nextBooking.get().getStart())
                    .end(nextBooking.get().getEnd())
                    .build());
        }

        itemDtoList.sort(Comparator.comparing(o -> o.getLastBooking().getStart(),
                Comparator.nullsLast(Comparator.reverseOrder())));

        for (ItemDto itemDto : itemDtoList) {
            if (itemDto.getLastBooking().getBookerId() == null) {
                itemDto.setLastBooking(null);
            }
            if (itemDto.getNextBooking().getBookerId() == null) {
                itemDto.setNextBooking(null);
            }
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> search(Long userId, String text) throws UserNotFoundException {
        User repoUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        if (text.isEmpty()) return Collections.emptyList();
        List<Item> searchItems = itemRepository.searchAvailableByText(text);
        List<ItemDto> searchItemDto = new ArrayList<>();
        for (Item item : searchItems) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setOwner(item.getOwner().getId());
            searchItemDto.add(itemDto);
        }
        return searchItemDto.isEmpty() ? Collections.emptyList() : searchItemDto;
    }

    @Override
    @Transactional
    public CommentDto comment(Long userId, Long itemId, CommentDto commentDto)
            throws ItemNotFoundException, UserNotFoundException, InvalidCommentException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("item not found"));
        User author = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        Sort sortDesc = Sort.by(Sort.Direction.DESC, "end");
        Booking booking = bookingRepository.findTop1BookingByItem_IdAndBooker_IdAndEndIsBeforeAndStatusIs(
                itemId, userId, LocalDateTime.now(), Status.APPROVED, sortDesc).orElseThrow(
                () -> new InvalidCommentException("no booking for comment"));

        Comment comment = CommentMapper.toComment(commentDto, item, author, LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

}

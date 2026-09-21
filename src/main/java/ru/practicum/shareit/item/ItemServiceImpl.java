package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Пользователь не найден"
                        )
                );

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Название вещи обязательно"
            );
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Описание вещи обязательно"
            );
        }

        if (itemDto.getAvailable() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Поле available обязательно"
            );
        }

        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequestId(itemDto.getRequestId());

        Item savedItem = itemRepository.save(item);

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Вещь не найдена"
                        )
                );

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Изменять вещь может только её владелец"
            );
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Вещь не найдена"
                        )
                );

        ItemDto dto = ItemMapper.toItemDto(item);

        List<Comment> comments =
                commentRepository.findByItemIdOrderByCreatedDesc(itemId);

        dto.setComments(
                comments.stream()
                        .map(this::toCommentDto)
                        .toList()
        );

        return dto;
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        LocalDateTime now = LocalDateTime.now();

        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);

                    Booking lastBooking =
                            bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                                    item.getId(),
                                    now,
                                    BookingState.APPROVED
                            );

                    Booking nextBooking =
                            bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                                    item.getId(),
                                    now,
                                    BookingState.APPROVED
                            );

                    dto.setLastBooking(toShortDto(lastBooking));
                    dto.setNextBooking(toShortDto(nextBooking));

                    List<Comment> comments =
                            commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

                    dto.setComments(
                            comments.stream()
                                    .map(this::toCommentDto)
                                    .toList()
                    );

                    return dto;
                })
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        text,
                        text
                )
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private BookingShortDto toShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingShortDto dto = new BookingShortDto();

        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());

        return dto;
    }

    @Override
    public CommentDto addComment(long userId, long itemId, String text) {
        User author = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Пользователь не найден"
                        )
                );

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Вещь не найдена"
                        )
                );

        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Комментарий не может быть пустым"
            );
        }

        boolean rented = bookingRepository
                .existsByItemIdAndBookerIdAndEndBeforeAndStatus(
                        itemId,
                        userId,
                        LocalDateTime.now(),
                        BookingState.APPROVED
                );

        if (!rented) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Оставить комментарий может только пользователь, который арендовал вещь"
            );
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return toCommentDto(savedComment);
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();

        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());

        return dto;
    }
}
package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto createRequest(long userId, ItemRequestDto requestDto) {
        User requester = userService.getUserById(userId);

        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setCreated(LocalDateTime.now());
        request.setRequester(requester);

        return toDto(itemRequestRepository.save(request), List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequests(long userId) {
        userService.getUserById(userId);

        return toDtos(itemRequestRepository
                .findByRequesterIdNotOrderByCreatedDesc(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(long userId) {
        userService.getUserById(userId);

        return toDtos(itemRequestRepository
                .findByRequesterIdNotOrderByCreatedDesc(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequest(long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Запрос не найден"
                ));

        return toDto(request, itemRepository.findByRequestId(requestId));
    }

    private List<ItemRequestDto> toDtos(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> ids = requests.stream().map(ItemRequest::getId).toList();

        Map<Long, List<Item>> itemsByRequest = itemRepository
                .findByRequestIdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        return requests.stream()
                .map(r -> toDto(r, itemsByRequest.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    private ItemRequestDto toDto(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = new ItemRequestDto();

        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items.stream().map(this::toItemRequestItemDto).toList());

        return dto;
    }

    private ItemRequestItemDto toItemRequestItemDto(Item item) {
        ItemRequestItemDto dto = new ItemRequestItemDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());

        return dto;
    }
}
package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.ShareItApp.REQUEST_NOT_EXISTS;
import static ru.practicum.shareit.ShareItApp.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Integer requesterId, ItemRequestSimpleDto dto, LocalDateTime now) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException(
                        format(USER_NOT_EXISTS, requesterId)));
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toEntity(dto, requesterId);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(now);

        return ItemRequestMapper.INSTANCE.toDto(
                itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto get(Integer requesterId, Integer itemRequestId) {
        checkingUserExists(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequesterIdNot(requesterId);

        return itemRequestRepository.findById(itemRequestId)
                .map(itemRequest -> {
                    itemRequest.setItems(itemRepository.getByRequest_Id(itemRequest.getId()));
                    return ItemRequestMapper.INSTANCE.toDto(itemRequest);
                })
                .orElseThrow(() -> new NotFoundException(
                        format(REQUEST_NOT_EXISTS, itemRequestId)));
    }

    @Override
    public List<ItemRequestDto> getByRequesterId(Integer requesterId) {
        checkingUserExists(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequesterId(requesterId);

        return getItemRequestDtoRecordList(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAll(Integer requesterId, Pageable pageable) {
        checkingUserExists(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequesterIdNot(requesterId);

        return getItemRequestDtoRecordList(itemRequests);
    }

    private List<ItemRequestDto> getItemRequestDtoRecordList(List<ItemRequest> requests) {
        Map<ItemRequest, List<Item>> requestListMap = itemRepository.findByRequestIn(requests)
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));

        requests.forEach(request ->
                request.setItems(
                        requestListMap.getOrDefault(request, emptyList())));

        return requests.stream().map(ItemRequestMapper.INSTANCE::toDto).collect(toList());
    }

    private void checkingUserExists(Integer requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException(
                    format(USER_NOT_EXISTS, requesterId));
        }
    }
}

package ru.practicum.shareit.item.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.api.dto.BookingMapper;
import ru.practicum.shareit.booking.api.dto.BookingToItemDto;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.comment.api.dto.CommentDtoRecord;
import ru.practicum.shareit.item.comment.api.dto.CommentMapper;
import ru.practicum.shareit.item.comment.api.dto.CommentSimpleDto;
import ru.practicum.shareit.item.comment.api.repository.CommentRepository;
import ru.practicum.shareit.item.comment.entity.CommentEntity;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.*;
import static ru.practicum.shareit.ShareItApp.ITEM_WITH_ID_NOT_EXIST;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.APPROVED;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final Sort sortStartAsc =
            Sort.by(Sort.Direction.ASC, "start");
    private final Sort sortStartDesc =
            Sort.by(Sort.Direction.DESC, "start");
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper = ItemMapper.COPY;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    /**
     * Создание предмета
     * <p>
     * Все поля обязательны к заполнению!
     *
     * @param userId  Идентификатор владелец предмета
     * @param itemDto Данные нового объекта
     * @return Созданный объект, после всех проверок условий
     */
    @Override
    public ItemDto create(Integer userId, ItemSimpleDto itemDto) {
        log.debug("[i] CREATE ITEM:{} by User.id:{}", itemDto, userId);
        checkingExistUserById(userId);

        return itemMapper.toDto(
                itemRepository.save(
                        itemMapper.toEntity(itemDto, userId)));
    }

    /**
     * Updating an item
     * <p>
     * Restrictions: only the owner of the item can edit it! <br/>
     * <ul>Partial editing is allowed:
     *     <li>{@link ItemDto#name} Name</li>
     *     <li>{@link ItemDto#description} Description</li>
     *     <li>{@link ItemDto#available} Visibility for all users</li>
     * </ul>
     *
     * @param userId  Идентификатор владелец предмета
     * @param itemDto ITEM DTO
     *                Название предмета
     *                Описание предмета
     *                Доступность предмета
     * @return itemDTO
     */
    @Override
    public ItemDto update(Integer userId, Integer itemId, ItemSimpleDto itemDto) {
        log.info("[i] UPDATE ITEM");
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        checkingExistUserById(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format(ITEM_WITH_ID_NOT_EXIST, itemId)));

        boolean isNotOwner = itemRepository.notExistsByIdAndOwner_Id(itemId, userId);
        if (isNotOwner) {
            throw new BadRequestException("Editing an item is only allowed to the owner of that item.");
        }

        boolean notNullDescription = !(description == null || description.isEmpty());
        boolean notNullName = !(name == null || name.isBlank());

        if (notNullName) {
            if (notNullDescription) {
                if (available != null) {

                    return itemMapper.toDto(itemRepository.save(itemMapper.toEntity(itemDto, userId)));

                } else {
                    log.info("[i] Name = {}, Description = {};", name, description);
                    itemRepository.updateNameAndDescriptionById(name, description, itemId);
                    item.setName(name);
                    item.setDescription(description);
                }
            } else {
                if (available != null) {
                    log.info("[i] Name = {}, Available = {};", name, available);
                    itemRepository.updateNameAndAvailableById(name, available, itemId);
                    item.setName(name);
                    item.setAvailable(available);
                } else {
                    log.info("[i] Name = {};", name);
                    itemRepository.updateNameById(name, itemId);
                    item.setName(name);
                }
            }
        } else {
            if (notNullDescription) {
                if (available != null) {
                    log.info("[i] Update Description = {}, Available = {};", description, available);
                    itemRepository.updateDescriptionAndAvailableById(description, available, itemId);
                    item.setDescription(description);
                    item.setAvailable(available);
                } else {
                    log.info("[i] Description = {};", description);
                    itemRepository.updateDescriptionById(description, itemId);
                    item.setDescription(description);
                }
            } else {
                if (available != null) {
                    log.info("[i] Available = {};", available);
                    itemRepository.updateAvailableById(available, itemId);
                    item.setAvailable(available);
                }
            }
        }

        return itemMapper.toDto(item);
    }

    /**
     * Receiving an item with the latest booking and comments.
     * Target:
     * {@code (/items/{id})}
     *
     * @param userId User ID
     * @param itemId Item ID
     * @return Item with/without Booking
     */
    @Override
    public ItemDto get(Integer userId, Integer itemId) {

        checkingExistUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException(String.format(ITEM_WITH_ID_NOT_EXIST, itemId)));
        ItemDto itemDto = itemMapper.toDto(item);

        BookingToItemDto lastBooking = bookingRepository
                .findFirstByItem_IdAndStartLessThanEqualAndStatus(itemId, now, APPROVED, sortStartDesc)
                .stream()
                .map(bookingMapper::toItemDto)
                .findFirst()
                .orElse(null);
        BookingToItemDto nextBooking = bookingRepository
                .findFirstByItem_IdAndStartAfterAndStatus(itemId, now, APPROVED, sortStartAsc)
                .stream()
                .map(bookingMapper::toItemDto)
                .findFirst()
                .orElse(null);
        List<CommentDtoRecord> comments = commentRepository
                .findAllByItem_IdOrderByCreatedDesc(itemId)
                .stream()
                .map(commentMapper::toDtoRecord)
                .collect(Collectors.toList());

        boolean isUserByOwnerByItem = item.getOwner().getId().equals(userId);
        if (isUserByOwnerByItem) {
            itemDto.setLastBooking(lastBooking);
            itemDto.setNextBooking(nextBooking);
        }
        itemDto.setComments(comments);

        return itemDto;
    }

    /**
     * Getting a list of items with the latest booking and comments
     * for both the owner of the items and users.
     * {@code (/items)}
     *
     * @param userId User ID
     * @return List of user's items
     */

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAll(Integer userId) {
        final LocalDateTime now = LocalDateTime.now();
        List<ItemDto> itemsDto = itemRepository.findAllByOwner_Id(userId)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());

        List<Integer> itemIds = itemsDto.stream()
                .map(ItemDto::getId)
                .collect(toList());

        Map<Integer, BookingToItemDto> lastBookingStorage = bookingRepository
                .findByItem_IdInAndStartLessThanEqualAndStatus(
                        itemIds, now, APPROVED, sortStartDesc)
                .stream()
                .map(bookingMapper::toItemDto)
                .collect(toMap(BookingToItemDto::getItemId,
                        Function.identity(),
                        (first, second) -> first));

        Map<Integer, BookingToItemDto> nextBookingStorage = bookingRepository
                .findByItem_IdInAndStartAfterAndStatus(
                        itemIds, now, APPROVED, sortStartAsc)
                .stream()
                .map(bookingMapper::toItemDto)
                .collect(toMap(BookingToItemDto::getItemId,
                        Function.identity(),
                        (first, second) -> first));

        Map<Integer, List<CommentEntity>> commentStorage = commentRepository
                .findByItem_IdInOrderByCreatedDesc(itemIds)
                .stream()
                .filter(Objects::nonNull)
                .collect(groupingBy((comment) -> comment.getItem().getId(), toList()));

        itemsDto.forEach(itemDto -> {
            Integer itemId = itemDto.getId();
            itemDto.setLastBooking(lastBookingStorage.get(itemId));
            itemDto.setNextBooking(nextBookingStorage.get(itemId));
            itemDto.setComments(
                    getCommentDtoRecords(commentStorage.get(itemId)));
        });

        return itemsDto;
    }

    /**
     * Creating a list of comments for the backend
     *
     * @param comments comments from DB
     * @return list of comments for the backend
     */
    private List<CommentDtoRecord> getCommentDtoRecords(
            List<CommentEntity> comments) {
        if (comments == null) {

            return null;
        }

        return comments.stream()
                .map(commentMapper::toDtoRecord)
                .collect(Collectors.toList());
    }

    /**
     * Deleting a user's item
     * <p>
     * Exception: only the owner of the item can delete it!
     *
     * @param userId Идентификатор пользователя
     * @param itemId Идентификатор предмета
     */
    @Override
    public void delete(Integer userId, Integer itemId) {
        log.debug("[i] DELETE Item.Id:{} by User.Id:{}", itemId, userId);

        checkingExistItemById(itemId);
        checkingExistUserById(userId);

        boolean isNotOwnerThisItem = !itemRepository.notExistsByIdAndOwner_Id(itemId, userId);
        if (isNotOwnerThisItem) {
            throw new BadRequestException(
                    "Edit or remove an item is only allowed to the owner of that item.");
        }

        itemRepository.deleteByIdAndOwner_Id(itemId, userId);
    }

    /**
     * Search for an item in the repository
     * <p>
     * If the query string is empty, output an empty list
     *
     * @param searchText текст для поиска
     * @return Список найденных вещей
     */
    @Override
    public List<ItemSimpleDto> search(String searchText) {
        log.debug("[i] SEARCH text:{}", searchText);
        if (searchText.isBlank()) {

            return List.of();
        }

        return itemRepository
                .searchItemByNameOrDescription(searchText)
                .stream()
                .map(itemMapper::toSimpleDto)
                .collect(toList());
    }

    /**
     * Adding a comment to the subject from the user,
     * who rented it at least 1 time.
     *
     * @param commentSimpleDto Comment DTO Source
     * @return Comment DTO Record
     */
    @Transactional
    @Override
    public CommentDtoRecord createComment(CommentSimpleDto commentSimpleDto) {
        String text = commentSimpleDto.getText().trim();
        if (text.isBlank()) {
            throw new BadRequestException("Text comment can't be blank");
        }
        Integer authorId = commentSimpleDto.getAuthorId();
        Integer itemId = commentSimpleDto.getItemId();
        commentSimpleDto.setText(text);
        log.info("[i] CREATE COMMENT USER_ID:{}, ITEM_ID:{}, DTO:{}", authorId, itemId, commentSimpleDto);

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(
                        format(USER_WITH_ID_NOT_EXIST, authorId)));
        checkingExistItemById(itemId);

        boolean notExistBooking = !bookingRepository
                .existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
                        itemId, authorId, APPROVED, LocalDateTime.now());
        if (notExistBooking) {
            throw new BadRequestException(
                    format("User with ID:(%d) has never booked an item with ID:(%d)\n" +
                                    "either the user has not completed the booking yet\n" +
                                    "or the user is planning to book this item",
                            authorId, itemId));
        }
        CommentEntity comment = commentMapper.toEntity(commentSimpleDto);
        comment.setAuthor(author);

        return commentMapper.toDtoRecord(
                commentRepository.save(comment));
    }

    /**
     * Checking for existence of a user in the repository
     *
     * @param userId User ID
     */
    private void checkingExistUserById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(
                    format(USER_WITH_ID_NOT_EXIST, userId));
        }
    }

    /**
     * Checking for existence of an item in the repository
     *
     * @param itemId Item ID
     */
    private void checkingExistItemById(Integer itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(
                    format(ITEM_WITH_ID_NOT_EXIST, itemId));
        }
    }
}

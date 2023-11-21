package ru.practicum.shareit.item.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.dto.BookingMapper;
import ru.practicum.shareit.booking.api.dto.BookingToItemDto;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.*;
import ru.practicum.shareit.item.api.repository.CommentRepository;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.ShareItApp.ITEM_WITH_ID_NOT_EXIST;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;
import static ru.practicum.shareit.booking.api.BookingServiceImpl.NOW;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.APPROVED;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
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
    public ItemDto create(Integer userId, ItemDto itemDto) {
        log.debug("[i] CREATE ITEM:{} by User.id:{}", itemDto, userId);

        checkingExistUserById(userId);
        Item item = itemRepository.save(
                itemMapper.toEntity(itemDto, userId));
        log.info("[i] CREATE ITEM (ID:{}) SUCCESSFUL (OWNER_ID:{})", item.getId(), item.getOwner().getId());

        return itemMapper.toDto(item);
    }

    /**
     * Обновление предмета
     * <p>
     * Ограничения: только владелец вещи может её редактировать! <br/>
     * <ul>Разрешено частичное редактирование:
     *     <li>Название</li>
     *     <li>Описание</li>
     *     <li>Видимость для всех пользователей</li>
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
    public ItemDto update(Integer userId, Integer itemId, ItemDto itemDto) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.isAvailable();

        checkingExistUserById(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format(ITEM_WITH_ID_NOT_EXIST, itemId)));

        if (!item.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Editing an item is only allowed to the owner of that item.");
        }

        boolean notNullDescription = !(description == null || description.isEmpty());
        boolean notNullName = !(name == null || name.isBlank());

        if (notNullName) {
            if (notNullDescription) {
                if (available != null) {

                    return itemMapper.toDto(itemRepository.save(itemMapper.toEntity(itemDto, userId)));

                } else {
                    log.info("[i] Update Name = {} + Description = {};", name, description);
                    itemRepository.updateNameAndDescriptionById(name, description, itemId);
                    item.setName(name);
                    item.setDescription(description);
                }
            } else {
                if (available != null) {
                    log.info("[i] Update Name = {} + Available = {};", name, available);
                    itemRepository.updateNameAndAvailableById(name, available, itemId);
                    item.setName(name);
                    item.setAvailable(available);
                } else {
                    log.info("[i] Update Name = {};", name);
                    itemRepository.updateNameById(name, itemId);
                    item.setName(name);
                }
            }
        } else {
            if (notNullDescription) {
                if (available != null) {
                    log.info("[i] Update Description = {} + Available = {};", description, available);
                    itemRepository.updateDescriptionAndAvailableById(description, available, itemId);
                    item.setDescription(description);
                    item.setAvailable(available);
                } else {
                    log.info("[i] Update Description = {};", description);
                    itemRepository.updateDescriptionById(description, itemId);
                    item.setDescription(description);
                }
            } else {
                if (available != null) {
                    log.info("[i] Update Available = {};", available);
                    itemRepository.updateAvailableById(available, itemId);
                    item.setAvailable(available);
                } else {
                    log.info("[i] Nothing update. All data is null");
                }
            }
        }

        return itemMapper.toDto(item);
    }

    /**
     * Получение предмета.
     * Target:
     * {@code (/items/{id})}
     *
     * @param userId User ID
     * @param itemId Item ID
     * @return Item with/without Booking
     */
    @Override
    public ItemDto get(Integer userId, Integer itemId) {
        log.debug("[i] GET ITEM.id:{}", itemId);
        boolean isOwnerIdOfItem;
        checkingExistUserById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        format(ITEM_WITH_ID_NOT_EXIST, itemId)));

        ItemDto itemDto = itemMapper.toDto(item);

        isOwnerIdOfItem = item.getOwner().getId().equals(userId);

        if (isOwnerIdOfItem) {
            itemDto.setLastBooking(getLastBookingList(itemId));
            itemDto.setNextBooking(getNextBookingList(itemId));
        }
        itemDto.setComments(
                getCommentDtoList(List.of(itemId)));

        return itemDto;
    }

    /**
     * Удаление предмета пользователя
     * <p>
     * Ограничения: только владелец вещи может её удалить!
     *
     * @param userId Идентификатор пользователя
     * @param itemId Идентификатор предмета
     */
    @Override
    public void delete(Integer userId, Integer itemId) {
        log.debug("[i] DELETE Item.Id:{} by User.Id:{}", itemId, userId);

        checkingExistItemById(itemId);
        checkingExistUserById(userId);

        boolean isNotOwnerThisItem = !itemRepository.existsByIdAndOwner_Id(itemId, userId);
        if (isNotOwnerThisItem) {
            throw new BadRequestException(
                    "Editing an item is only allowed to the owner of that item.");
        }

        itemRepository.deleteByIdAndOwner_Id(userId, itemId);
    }

    /**
     * Поиск предмета в репозитории
     * <p>
     * Если строка запроса пуста, выводить пустой список
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
                .findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrueOrderByIdAsc(
                        searchText, searchText)
                .stream()
                .map(itemMapper::toSimpleDto)
                .collect(toList());
    }

    /**
     * Добавление комментария к предмету от пользователя,
     * бравшего его в аренду хотя бы 1 раз.
     *
     * @param userId     User ID - Author
     * @param itemId     Item ID
     * @param commentDto Comment DTO
     * @return Комментарий
     */
    @Override
    public CommentDto createComment(Integer userId, Integer itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format(USER_WITH_ID_NOT_EXIST, userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format(ITEM_WITH_ID_NOT_EXIST, itemId)));

        boolean booking = bookingRepository
                .existsByBooker_IdAndItem_IdAndEndBeforeAndStatus(
                        userId, itemId, NOW, APPROVED);
        if (!booking) {
            String error = format("A user with an ID:(%d) has never rented an item with an ID:(%d)",
                    userId, itemId);
            throw new BadRequestException(error);
        }
        CommentEntity comment = CommentEntity.builder()
                .author(author)
                .item(item)
                .text(commentDto.getText())
                .build();

        return commentMapper.toDto(commentRepository.save(comment));
    }

    /**
     * Получение списка всех доступных предметов в аренду от пользователя
     * {@code (/items)}
     *
     * @param userId Идентификатор пользователя
     * @return Список предметов пользователя
     */
    @Override
    public List<ItemDto> getAll(Integer userId) {
        final boolean userIsOwnerByItems;
        List<ItemDto> itemDtoList;
        List<Integer> itemDtoIds;
        Map<Integer, BookingToItemDto> lastBookingStorage = new HashMap<>();
        Map<Integer, BookingToItemDto> nextBookingStorage = new HashMap<>();
        Map<Integer, List<CommentDto>> itemCommentsStorage = new HashMap<>();

        log.debug("[i] GET ALL ITEMS by User.id:{}", userId);

        checkingExistUserById(userId);
        userIsOwnerByItems = itemRepository.existsByOwner_Id(userId);

        if (userIsOwnerByItems) {
            log.info("[i] DTO LIST + BOOKING");
            itemDtoList = itemRepository.getByOwner_IdOrderByIdAsc(userId)
                    .stream().map(itemMapper::toDto).collect(toList());
        } else {
            log.info("DTO LIST");
            itemDtoList = itemRepository.getByAvailableTrueOrderByIdAsc()
                    .stream().map(itemMapper::toDto).collect(toList());
        }
        itemDtoIds = itemDtoList.stream()
                .map(ItemDto::getId)
                .collect(toList());

        itemDtoIds.forEach(i -> {
            if (userIsOwnerByItems) {
                lastBookingStorage.put(i, getLastBookingList(i));
                nextBookingStorage.put(i, getNextBookingList(i));
            }
            itemCommentsStorage.put(i, null);
        });

        for (CommentDto dto : getCommentDtoList(itemDtoIds)) {
            if (dto != null) {
                Integer currentItem = dto.getItemId();
                itemCommentsStorage.get(currentItem).add(dto);
            }
        }
        itemDtoList.forEach(
                itemDto -> {
                    Integer itemId = itemDto.getId();
                    BookingToItemDto last = lastBookingStorage.get(itemId);
                    BookingToItemDto next = nextBookingStorage.get(itemId);
                    if (userIsOwnerByItems && (last != null && next != null)) {
                        log.info("[i] BEFORE SET BOOKINGS:\n" +
                                        "LAST (ID:{}, USER_ID:{}, ITEM_ID:{})\n" +
                                        "NEXT (ID:{}, USER_ID:{}, ITEM_ID:{})",
                                last.getId(), last.getBookerId(), last.getItemId(),
                                next.getId(), next.getBookerId(), next.getItemId());
                        itemDto.setLastBooking(last);
                        itemDto.setNextBooking(next);
                    }
                    itemDto.setComments(itemCommentsStorage.get(itemId));
                }
        );

        return itemDtoList;
    }

    private BookingToItemDto getLastBookingList(Integer itemId) {
        Optional<Booking> booking = bookingRepository
                .getFirstByItem_IdAndStartAfterAndStatusOrderByIdAsc(
                        itemId, NOW, APPROVED);

        return booking.map(value -> bookingMapper.toItemDto(value, itemId)).orElse(null);
    }

    private BookingToItemDto getNextBookingList(Integer itemId) {
        Optional<Booking> booking = bookingRepository
                .getFirstByItem_IdAndStartGreaterThanEqualAndStatusOrderByIdDesc(
                        itemId, NOW, APPROVED);

        return booking.map(value -> bookingMapper.toItemDto(value, itemId)).orElse(null);
    }

    private List<CommentDto> getCommentDtoList(List<Integer> itemIds) {

        return commentRepository.getByItem_IdIn(itemIds).stream()
                .map(commentMapper::toDto)
                .collect(toList());
    }

    /**
     * Проверка на существование в репозитории
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
     * Проверка на существование в репозитории
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

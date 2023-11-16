package ru.practicum.shareit.item.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.CommentDto;
import ru.practicum.shareit.item.api.dto.CommentMapper;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.repository.CommentRepository;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;

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
        isExistOwnerUser(userId);

        return itemMapper.toDto(
                itemRepository.save(
                        itemMapper.toEntity(itemDto, userId)));
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
//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @Override
    public ItemDto update(Integer userId, Integer itemId, ItemDto itemDto) { // String name, String description, Boolean available) { //ItemDto itemDto) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.isAvailable();

        checkingExistById(userRepository.existsById(userId),
                format("User with id:(%d) not exist", userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException(format("Item with id:(%d) not exist", itemId))
                );
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessException("Editing an item is only allowed to the owner of that item.");
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
     * Получение предмета
     *
     * @param itemId идентификатор предмета
     * @return предмет
     */
    @Override
    public ItemDto get(Integer itemId) {
        log.debug("[i] GET ITEM.id:{}", itemId);
        String error = format("Item with id:(%d) not exist", itemId);

        return itemMapper.toDto(
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new NotFoundException(error))
        );
    }

    /**
     * Получение списка всех доступных предметов в аренду от пользователя
     *
     * @param userId Идентификатор пользователя
     * @return Список предметов пользователя
     */
    @Override
    public List<ItemDto> getAll(Integer userId) {
        log.debug("[i] GET ALL ITEMS by User.id:{}", userId);
        return itemRepository.findDistinctByOwner_IdAndAvailableTrueOrderByIdAsc(userId)
                .stream().map(itemMapper::toDto)
                .collect(toList());
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

        String errorItem = format("Item with id:(%d) not exist", itemId);
        checkingExistById(itemRepository.existsById(itemId), errorItem);

        getItemAfterCheckingOwner(userId, itemId);

        String errorUser = format("User with id:(%d) not exist", userId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(errorUser));

        itemRepository.deleteByIdAndOwner(itemId, owner);
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
    public List<ItemDto> search(String searchText) {
        log.debug("[i] SEARCH text:{}", searchText);
        if (searchText.isBlank()) {
            return List.of();
        }

        return itemRepository.findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrueOrderByIdAsc(
                        searchText, searchText)
                .stream()
                .map(itemMapper::toDto)
                .collect(toList());
    }

    @Override
    public CommentDto createComment(Integer userId, Integer itemId, String text) {
        checkingExistById(userRepository.existsById(userId),
                format("User with id:(%d) not exist", userId));
        checkingExistById(itemRepository.existsById(itemId),
                format("Item with id:(%d) not exist", itemId));

        boolean booking = bookingRepository.existsByItem_IdAndBooker_Id(itemId, userId);
        if (!booking) {
            String error = format("A user with an ID:(%d) has never rented an item with an ID:(%d)", userId, itemId);
            throw new NotFoundException(error);
        }
        CommentEntity comment = CommentEntity.builder()
                .authorId(userId)
                .itemId(itemId)
                .commentText(text).build();
        return commentMapper.toDto(commentRepository.save(comment));
    }

    /**
     * Проверка на существование в репозитории
     *
     * @param isExist Условие проверки (true if not exists)
     * @param error   Текст ошибки
     */
    private void checkingExistById(boolean isExist, String error) {
        if (!isExist) {
            throw new NotFoundException(error);
        }
    }

    /**
     * Проверка принадлежности предмета пользователю
     *
     * @param userId Идентификатор пользователя
     * @param itemId Идентификатор предмета
     * @see #update(Integer, Integer, ItemDto) todo: another (Integer, Integer, String, String, Boolean)
     * @see #delete(Integer, Integer)
     */
    private void getItemAfterCheckingOwner(Integer userId, Integer itemId) {
        if (!itemRepository.existsByIdAndOwner_Id(itemId, userId)) {
            throw new AccessException("Editing an item is only allowed to the owner of that item.");
        }
    }

    /**
     * Проверка на указание пользователя в заголовке запроса
     *
     * @param userId Идентификатор пользователя
     */
    private void isExistOwnerUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("userId:" + userId + " not found");
        }
    }
}

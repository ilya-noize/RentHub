package ru.practicum.shareit.item.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
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

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
     * @param userId    Идентификатор владелец предмета
     * @param itemDto   Данные нового объекта
     * @return          Созданный объект, после всех проверок условий
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
     * @param userId    Идентификатор владелец предмета
     * @param itemDto   Данные изменённого объекта
     * @return          Изменённый объект
     */
    @Override
    public ItemDto update(Integer userId, Integer itemId, ItemDto itemDto) {
        log.info("[i] UPDATE ITEM\nUserId:{}\nItemId:{}\nItemDto:{}", userId, itemId, itemDto.toString());
        checkingExistById(itemRepository.existsById(itemId),
                format("Item with id:(%d) not exist", itemId));
        checkingExistById(userRepository.existsById(userId),
                format("User with id:(%d) not exist", userId));

        Item itemFromRepository = getItemAfterCheckingOwner(userId, itemId);
        String name = itemDto.getName();
        if (name == null || name.isBlank()) {
            log.info("[i] Name correct");
            itemDto.setName(itemFromRepository.getName());
        }
        String description = itemDto.getDescription();
        if (description == null || description.isEmpty()) {
            log.info("[i] Description correct");
            itemDto.setDescription(itemFromRepository.getDescription());
        }
        if (itemDto.isAvailable() == null) {
            log.info("[i] Available correct");
            itemDto.setAvailable(itemFromRepository.isAvailable());
        }

        return itemMapper.toDto(itemRepository.save(itemMapper.toEntity(itemDto, userId)));
    }

    /**
     * Получение предмета
     * @param id идентификатор предмета
     * @return предмет
     */
    @Override
    public ItemDto get(Integer id) {
        log.debug("[i] GET ITEM.id:{}", id);
        isExistItem(id);

        return itemMapper.toDto(itemRepository.getReferenceById(id));
    }

    /**
     * Получение списка всех доступных предметов в аренду от пользователя
     * @param userId Идентификатор пользователя
     * @return Список предметов пользователя
     */
    @Override
    public List<ItemDto> getAll(Integer userId) {
        log.debug("[i] GET ALL ITEMS by User.id:{}", userId);
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .filter(Item::isAvailable)
                .map(itemMapper::toDto)
                .collect(toList());
    }

    /**
     * Удаление предмета пользователя
     * <p>
     * Ограничения: только владелец вещи может её удалить!
     * @param userId Идентификатор пользователя
     * @param itemId Идентификатор предмета
     */
    @Override
    public void delete(Integer userId, Integer itemId) {
        log.debug("[i] DELETE Item.Id:{} by User.Id:{}", itemId, userId);
        checkingExistById(itemRepository.existsById(itemId),
                format("Item with id:(%d) not exist", itemId));
        getItemAfterCheckingOwner(userId, itemId);
        itemRepository.deleteById(itemId);
    }

    /**
     * Поиск предмета в репозитории
     * <p>
     *     Если строка запроса пуста, выводить пустой список
     * @param searchText текст для поиска
     * @return Список найденных вещей
     */
    @Override
    public List<ItemDto> search(String searchText) {
        log.debug("[i] SEARCH text:{}", searchText);
        if (searchText.isBlank()) {
            return List.of();
        }
        Predicate<Item> searchPredicate = item ->
                item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(searchText.toLowerCase());

        return itemRepository.findAll().stream()
                .filter(searchPredicate)
                .filter(Item::isAvailable)
                .map(itemMapper::toDto)
                .collect(toList());
    }

    @Override
    public CommentDto createComment(Integer userId, Integer itemId, String text) {
        checkingExistById(userRepository.existsById(userId),
                format("User with id:(%d) not exist", userId));
        checkingExistById(itemRepository.existsById(itemId),
                format("Item with id:(%d) not exist", itemId));

        Optional<Booking> booking = bookingRepository.findByItem_IdAndBooker_Id(itemId, userId);
        if (booking.isEmpty()) {
            String error = format("A user with an ID:(%d) has never rented an item with an ID:(%d)", userId, itemId);
            throw new NotFoundException(error);
        }
        CommentEntity comment = CommentEntity.builder()
                .authorId(userId)
                .itemId(itemId)
                .commentText(text).build();
        return commentMapper.toDto(commentRepository.save(comment));
    }

    private void checkingExistById(boolean isExist, String error) {
        if (!isExist) {
            throw new NotFoundException(error);
        }
    }

    /**
     * Проверка предмета на существование в репозитории
     * @param itemId Идентификатор предмета
     */
    private void isExistItem(Integer itemId) {
        log.debug("[i] is exist Item by ID:{}", itemId);
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(format("item.id(%d) is not exist!", itemId));
        }
    }

    /**
     * Проверка принадлежности предмета пользователю
     * @param userId Идентификатор пользователя
     * @param itemId Идентификатор предмета
     * @return Предмет из репозитория
     * @see #update(Integer, Integer, ItemDto)
     * @see #delete(Integer, Integer)
     */
    private Item getItemAfterCheckingOwner(Integer userId, Integer itemId) {
        if (!itemRepository.existsByIdAndUserId(itemId, userId)) {
            throw new AccessException("Editing an item is only allowed to the owner of that item.");
        }
        return itemRepository.getReferenceById(itemId);
    }

    /**
     * Проверка на указание пользователя в заголовке запроса
     * @param userId    Идентификатор пользователя
     */
    private void isExistOwnerUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("userId:" + userId + " not found");
        }
    }
}

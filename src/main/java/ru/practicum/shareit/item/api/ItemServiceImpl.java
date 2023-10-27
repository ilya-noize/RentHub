package ru.practicum.shareit.item.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.api.CRUDRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemMapperImpl;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final CRUDRepository<User> userStorage;
    private final CRUDRepository<Item> itemRepository;
    private final ItemMapperImpl mapper;

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
        isTheOwnerSpecified(userId);
        if (!userStorage.isExist(userId)) {
            throw new NotFoundException(String.format("user.id(%d) is not exist!", userId));
        }

        String name = itemDto.getName();
        if (name == null || name.isBlank()) {
            throw new NullPointerException("Name - skipped.");
        }
        String description = itemDto.getDescription();
        if (description == null || description.isBlank()) {
            throw new NullPointerException("Description - skipped.");
        }
        if (itemDto.isAvailable() == null) {
            throw new NullPointerException("Available - skipped");
        }

        return mapper.toDto(
                itemRepository.create(
                        mapper.toEntity(itemDto, userId)));
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
        isExist(itemId);
        isTheOwnerSpecified(userId);

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

        return mapper.toDto(itemRepository.update(itemId, mapper.toEntity(itemDto, userId)));
    }

    /**
     * Получение предмета
     * @param id идентификатор предмета
     * @return предмет
     */
    @Override
    public ItemDto get(Integer id) {
        log.debug("[i] GET ITEM.id:{}", id);
        isExist(id);

        return mapper.toDto(itemRepository.get(id));
    }

    /**
     * Получение списка всех доступных предметов в аренду от пользователя
     * @param userId Идентификатор пользователя
     * @return Список предметов пользователя
     */
    @Override
    public List<ItemDto> getAll(Integer userId) {
        log.debug("[i] GET ALL ITEMS by User.id:{}", userId);
        return itemRepository.getAll().stream()
                .filter(item -> item.getUserId().equals(userId))
                .filter(Item::isAvailable)
                .map(mapper::toDto)
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
        isExist(itemId);
        getItemAfterCheckingOwner(userId, itemId);
        itemRepository.delete(itemId);
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

        return itemRepository.getAll().stream()
                .filter(searchPredicate)
                .filter(Item::isAvailable)
                .map(mapper::toDto)
                .collect(toList());
    }

    /**
     * Проверка предмета на существование в репозитории
     * @param itemId Идентификатор предмета
     */
    private void isExist(Integer itemId) {
        log.debug("[i] is exist Item by ID:{}", itemId);
        if (!itemRepository.isExist(itemId)) {
            throw new NotFoundException(String.format("item.id(%d) is not exist!", itemId));
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
        Item itemFromRepository = itemRepository.get(itemId);
        if (!itemFromRepository.getUserId().equals(userId)) {
            throw new AccessException("Editing an item is only allowed to the owner of that item.");
        }
        return itemFromRepository;
    }

    /**
     * Проверка на указание пользователя в заголовке запроса
     * @param userId    Идентификатор пользователя
     */
    private void isTheOwnerSpecified(Integer userId) {
        if (userId == null) {
            throw new NotFoundException("You must specify the owner of the item.");
        } else if (!userStorage.isExist(userId)) {
            throw new NotFoundException("userId:" + userId + " not found");
        }
    }
}

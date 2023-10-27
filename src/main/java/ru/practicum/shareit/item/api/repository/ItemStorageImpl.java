package ru.practicum.shareit.item.api.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final Map<Integer, Item> items;
    private Integer id = 0;

    /**
     * Создание предмета
     * @param item  Предмет
     * @return      Созданный предмет
     */
    @Override
    public Item create(Item item) {
        id++;
        item = Item.builder()
                .id(id)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .userId(item.getUserId())
                .request(item.getRequest()).build();
        items.put(id, item);
        return get(id);
    }

    /**
     * Обновление предмета
     * @param item  Предмет
     * @return      Обновлённый предмет
     */
    @Override
    public Item update(Integer id, Item item) {
        item = Item.builder()
                .id(id)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .request(item.getRequest())
                .userId(item.getUserId())
                .build();
        items.replace(item.getId(), item);
        return get(item.getId());
    }

    /**
     * Получение предмета
     * @param id    Идентификатор предмета
     * @return      Предмет
     */
    @Override
    public Item get(Integer id) {
        Item item = items.get(id);
        log.info("[i] GET\nid:{}\nItem:{}", id, item);
        return item;
    }

    /**
     * Получение всех предметов
     * @return      Список предметов
     */
    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    /**
     * Удаление предмета
     * <p>
     * @param id    Идентификатор предмета
     */
    @Override
    public void delete(Integer id) {
        items.remove(id);
    }

    /**
     * Проверить существование предмета
     * @param id    Идентификатор предмета
     * @return      Да / Нет  aka  true / false
     */
    @Override
    public boolean isExist(Integer id) {
        return items.containsKey(id);
    }
}

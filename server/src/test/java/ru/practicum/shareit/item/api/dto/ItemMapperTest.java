package ru.practicum.shareit.item.api.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.entity.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.practicum.shareit.constants.Constants.RANDOM;

@SpringBootTest
class ItemMapperTest {
    private final ItemMapper itemMapper = ItemMapper.INSTANCE;

    @Test
    void toSimpleDto() {
        Item item = RANDOM.nextObject(Item.class);

        ItemSimpleDto result = itemMapper.toSimpleDto(item);

        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void toSimpleDto_null() {
        ItemSimpleDto result = itemMapper.toSimpleDto(null);

        assertNull(result);
    }

    @Test
    void toDto() {
        Item item = RANDOM.nextObject(Item.class);

        ItemDto result = itemMapper.toDto(item);

        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void toDto_null() {
        ItemSimpleDto result = itemMapper.toSimpleDto(null);

        assertNull(result);
    }

    @Test
    void toEntity() {
        ItemSimpleDto dto = RANDOM.nextObject(ItemSimpleDto.class);

        Item result = itemMapper.toEntity(dto, 1);

        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getName(), result.getName());
    }

    @Test
    void toEntity_null() {
        Item result = itemMapper.toEntity(null, 1);

        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getDescription());
        assertFalse(result.isAvailable());
    }
}
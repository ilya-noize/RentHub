package ru.practicum.shareit.item.api.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.entity.Item;

@Component
public class ItemMapperImpl implements ItemMapper {

    public ItemDto toDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    @Override
    public Item toEntity(ItemDto itemDto) {
        return null;
    }

    @Override
    public Item toEntity(ItemDto itemDto, Integer userId) {

        return Item.builder()
                .id(itemDto.getId() == null ? 0 : itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.isAvailable())
                .userId(userId)
                .build();
    }
}

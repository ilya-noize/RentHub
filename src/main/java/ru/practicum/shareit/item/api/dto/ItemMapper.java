package ru.practicum.shareit.item.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.practicum.shareit.item.entity.Item;

@Mapper(componentModel = "spring")
@Qualifier("ItemMapper")
public interface ItemMapper {
    ItemDto toDto(Item item);

    @Mapping(target = "userId", source = "userId")
    Item toEntity(ItemDto itemDto, Integer userId);
}

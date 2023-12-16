package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.request.entity.ItemRequest;

@Mapper(uses = ItemMapper.class)
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    ItemRequestDto toDto(ItemRequest entity);

    @Mapping(target = "requester.id", source = "requesterId")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    ItemRequest toEntity(ItemRequestSimpleDto dto, Long requesterId);
}

package ru.practicum.shareit.request.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.request.entity.ItemRequest;

@Mapper(uses = ItemMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(target = "items", ignore = true)
    ItemRequestDto toDto(ItemRequest entity);

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    ItemRequest toEntity(ItemRequestSimpleDto dto);
}

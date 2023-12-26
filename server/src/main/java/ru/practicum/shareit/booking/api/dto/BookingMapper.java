package ru.practicum.shareit.booking.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.api.dto.ItemMapper;

@Mapper(uses = {ItemMapper.class})
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "item.id", source = "dto.itemId")
    @Mapping(target = "booker.id", source = "bookerId")
    Booking toEntity(BookingSimpleDto dto, Long bookerId);

    BookingDto toDto(Booking entity);

    @Mapping(target = "itemId", source = "entity.item.id")
    @Mapping(target = "bookerId", source = "entity.booker.id")
    BookingItemDto toItemDto(Booking entity);
}

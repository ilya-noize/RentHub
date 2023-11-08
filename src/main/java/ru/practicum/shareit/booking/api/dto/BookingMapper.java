package ru.practicum.shareit.booking.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.entity.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "bookerId", source = "userId") // todo: add userId
    @Mapping(target = "itemId", source = "itemId") // todo: add itemId
    BookingDto toDto(Booking entity, Integer userId, Integer itemId);

    Booking toEntity(BookingDto dto);
}

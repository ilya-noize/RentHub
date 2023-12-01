package ru.practicum.shareit.booking.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.entity.Booking;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR)
public interface BookingMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "item.id", source = "dto.itemId")
    @Mapping(target = "booker.id", source = "bookerId")
    Booking toEntity(BookingDto dto, Integer bookerId);

    BookingDtoRecord toDtoRecord(Booking entity);

    @Mapping(target = "itemId", source = "entity.item.id")
    @Mapping(target = "bookerId", source = "entity.booker.id")
    BookingToItemDto toItemDto(Booking entity);
}

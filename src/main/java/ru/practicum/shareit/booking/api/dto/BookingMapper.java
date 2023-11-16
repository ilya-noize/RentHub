package ru.practicum.shareit.booking.api.dto;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.api.dto.ItemDto;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Slf4j
@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR)
public abstract class BookingMapper {

    @Mapping(target = "itemId", source = "entity.item.id")
    public abstract BookingDto toDto(Booking entity);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "item.id", source = "dto.itemId")
    @Mapping(target = "booker.id", source = "bookerId")
    public abstract Booking toEntity(BookingDto dto, Integer bookerId);

    public BookingDtoRecord toDtoRecord(Booking entity, Integer bookerId, ItemDto itemDto) {
        log.info("[i] MAPPER BOOKER\nID:{}, START:{}, END:{}, STATUS:{}, BOOKER_ID:{}, ITEM_ID:{}, ITEM_NAME:{}",
                entity.getId(), entity.getStart(), entity.getEnd(), entity.getStatus(), bookerId, itemDto.getId(), itemDto.getName());

        return new BookingDtoRecord(
                entity.getId(),
                entity.getStart(),
                entity.getEnd(),
                entity.getStatus(),
                new BookingDtoRecord.BookerDto(bookerId),
                new BookingDtoRecord.ItemDto(
                        itemDto.getId(),
                        itemDto.getName()
                )
        );
    }
}

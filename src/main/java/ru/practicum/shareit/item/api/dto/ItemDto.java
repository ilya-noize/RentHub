package ru.practicum.shareit.item.api.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.api.dto.BookingToItemDto;
import ru.practicum.shareit.item.comment.api.dto.CommentDtoRecord;

import java.util.List;

/**
 * DTO-Class Item.
 * Just a class with data. Don't touch him.
 * <p>
 * Used in Controller, Service;
 * <p>
 * Fields: <br/>
 * {@code id} ID Item <br/>
 * {@code name} Name item <br/>
 * {@code description} Description item <br/>
 * {@code available} Available item <br/>
 * {@code request} RequestId
 */

@Data
@Builder
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingToItemDto lastBooking;
    private BookingToItemDto nextBooking;
    private List<CommentDtoRecord> comments;
}

package ru.practicum.shareit.item.api.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.api.dto.BookingToItemDto;
import ru.practicum.shareit.item.comment.api.dto.CommentDtoRecord;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * DTO-Class Item.
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

@Getter
@Setter
@Builder()
public class ItemDto {
    @PositiveOrZero
    private Integer id;

    @NotBlank(groups = {Create.class})
    private String name;

    @NotBlank(groups = {Create.class})
    private String description;

    @Getter(AccessLevel.NONE)
    @NotNull(groups = {Create.class})
    private Boolean available;

    public Boolean isAvailable() {
        return this.available;
    }

    private BookingToItemDto lastBooking;
    private BookingToItemDto nextBooking;
    private List<CommentDtoRecord> comments;
}

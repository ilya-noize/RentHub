package ru.practicum.shareit.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.entity.BookingFilterByTemplate;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Create:
 *  itemId, start, end;
 */
@Data
@Builder
@AllArgsConstructor
public class BookingDtoStates {
    private Integer id;
    @NotNull(groups = {Create.class})
    @FutureOrPresent
    private Date start;
    @NotNull(groups = {Create.class})
    @Future
    private Date end;
    @NotNull(groups = {Create.class, Update.class})
    private Integer itemId;
    @NotNull(groups = {Create.class, Update.class})
    private Integer userId;
    private BookingFilterByTemplate states;
}

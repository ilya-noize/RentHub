package ru.practicum.shareit.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDto {
    private Integer id;
    @NotNull(groups = {Create.class})
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(groups = {Create.class})
    @Future
    private LocalDateTime end;
    @NotNull(groups = {Create.class, Update.class})
    private Integer itemId;
    @NotNull(groups = {Create.class, Update.class})
    private Integer bookerId;
    private BookingStatus status;
}

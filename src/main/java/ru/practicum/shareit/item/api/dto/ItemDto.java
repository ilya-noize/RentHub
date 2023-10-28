package ru.practicum.shareit.item.api.dto;

import lombok.*;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

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

@Data
@Builder
@AllArgsConstructor
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
}

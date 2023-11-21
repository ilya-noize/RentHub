package ru.practicum.shareit.item.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
public class ItemSimpleDto {
    @PositiveOrZero
    private Integer id;

    @NotBlank(groups = {Create.class})
    private String name;

    @NotBlank(groups = {Create.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

}

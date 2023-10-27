package ru.practicum.shareit.item.api.dto;

import lombok.*;

/**
 * DTO-Class Item.
 * <p>
 *     Used in Controller, Service;
 * <p>
 *     Fields: <br/>
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
    private Integer id;
    private String name;
    private String description;
    @Getter(AccessLevel.NONE)
    private Boolean available;
    private Integer request;

    public Boolean isAvailable() {
        return this.available;
    }
}

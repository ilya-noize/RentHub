package ru.practicum.shareit.item.api.dto;

import lombok.Builder;
import lombok.Data;

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
public class ItemSimpleDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}

package ru.practicum.shareit.request.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 * <p>
 * A class of user requests for items
 * <p>
 * {@code id} ID request <br/>
 * {@code created} Date of creation <br/>
 * {@code description} Request text <br/>
 * {@code requester} The author of the request <br/>
 */
@Builder
@Getter
@AllArgsConstructor
public class ItemRequest {
    private final Integer id;
    private final LocalDate created;
    private final String description;
    private final Integer requester;
}

package ru.practicum.shareit.item.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.request.entity.ItemRequest;

import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 * <p>
 * Класс Вещь.
 * <p>
 * {@code id} ID Item <br/>
 * {@code name} Item's name <br/>
 * {@code description} Item's description <br/>
 * {@code available} Item Availability <br/>
 * {@code owner} The owner of the item <br/>
 * {@code request} Request for this item for the user, if there was one <br/>
 */
@Builder
@Getter
@AllArgsConstructor
public class Item {
    @Positive
    private final int id;
    private final String name;
    private final String description;
    @Getter(AccessLevel.NONE)
    private final boolean available;
    private final Integer userId;
    private final ItemRequest request;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", userId=" + userId +
                ", request=" + request +
                '}';
    }

    public boolean isAvailable() {
        return this.available;
    }
}



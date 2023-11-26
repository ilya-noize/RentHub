package ru.practicum.shareit.item.entity;

import lombok.*;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;

/**
 * Класс Предмет.
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
@Setter
@AllArgsConstructor
@Entity
@Table(name = "items", schema = "public")
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    @Getter(AccessLevel.NONE)
    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private User owner;

    public boolean isAvailable() {
        return this.available;
    }
}



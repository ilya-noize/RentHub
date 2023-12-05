package ru.practicum.shareit.item.entity;

import lombok.*;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * {@link Item}Класс Предмет.
 * <p>
 * {@link Item#id} ID Item <br/>
 * {@link Item#name} Item's name <br/>
 * {@link Item#description} Item's description <br/>
 * {@link Item#available} Item Availability <br/>
 * {@link Item#owner} The owner of the item <br/>
 * {@link } Request for this item for the user, if there was one <br/>
 */

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = 255)
    private String name;

    @Size(max = 512)
    private String description;
    @Getter(AccessLevel.NONE)
    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private User owner;

    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "description = " + description + ", " +
                "available = " + available + ")";
    }
}



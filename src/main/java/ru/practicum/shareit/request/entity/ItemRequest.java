package ru.practicum.shareit.request.entity;

import lombok.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A class of user requests for items
 * <p>
 * {@code id} ID request <br/>
 * {@code created} Date of creation <br/>
 * {@code description} Request text <br/>
 * {@code requester} The author of the request <br/>
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    private LocalDateTime created;

    @Transient
    private List<Item> items;

    @Override
    public String toString() {
        return "ItemRequest{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", created=" + created +
                '}';
    }
}
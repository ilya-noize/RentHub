package ru.practicum.shareit.request.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
@Entity
@Table(name = "requests", schema = "public")
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private LocalDate created;
    private String description;
    private Integer requester;
}

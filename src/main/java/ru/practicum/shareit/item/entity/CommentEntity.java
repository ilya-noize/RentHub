package ru.practicum.shareit.item.entity;

import lombok.*;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENTS", schema = "PUBLIC")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 512)
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    private LocalDateTime created;
}

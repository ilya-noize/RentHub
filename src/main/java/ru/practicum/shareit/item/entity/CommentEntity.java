package ru.practicum.shareit.item.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;

@Entity
@Table(name = "COMMENTS", schema = "PUBLIC")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "comment_text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id",
            referencedColumnName = "id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id",
            referencedColumnName = "id")
    private User author;
}

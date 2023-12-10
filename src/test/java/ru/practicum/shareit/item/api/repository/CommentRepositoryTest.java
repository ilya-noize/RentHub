package ru.practicum.shareit.item.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.ShareItApp.RANDOM;

@DataJpaTest
class CommentRepositoryTest {
    private final LocalDateTime now = LocalDateTime
            .of(2000, 1, 1, 12, 0, 0, 0);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User getNewUser() {
        User user = RANDOM.nextObject(User.class);
        return userRepository.save(user);
    }

    private Item getNewItem(User owner) {
        Item item = RANDOM.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        return itemRepository.save(item);
    }

    private void getNewComment(User author, Item item) {
        CommentEntity comment = RANDOM.nextObject(CommentEntity.class);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(now);

        commentRepository.save(comment);
    }

    @Test
    void findByItem_IdInOrderByCreatedDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);
        User author = getNewUser();
        getNewComment(author, item1);

        assertEquals(1, commentRepository
                .findByItem_IdInOrderByCreatedDesc(List.of(item1.getId(), item2.getId()))
                .size());
    }

    @Test
    void findAllByItem_IdOrderByCreatedDesc() {
        User owner = getNewUser();
        Item item = getNewItem(owner);
        User author = getNewUser();
        getNewComment(author, item);

        assertEquals(1, commentRepository
                .findAllByItem_IdOrderByCreatedDesc(item.getId())
                .size());
    }
}
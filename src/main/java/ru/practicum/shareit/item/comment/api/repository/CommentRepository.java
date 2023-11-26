package ru.practicum.shareit.item.comment.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.comment.entity.CommentEntity;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    // for getAll  Items
    @Query("select c from CommentEntity c where c.item.id in ?1 order by c.created DESC")
    List<CommentEntity> findByItem_IdInOrderByCreatedDesc(List<Integer> itemIds);

    // for get  Item
    @Query("select c from CommentEntity c where c.item.id = ?1 order by c.created DESC")
    List<CommentEntity> findAllByItem_IdOrderByCreatedDesc(Integer itemId);
}
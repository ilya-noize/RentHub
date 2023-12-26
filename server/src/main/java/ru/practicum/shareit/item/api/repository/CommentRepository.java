package ru.practicum.shareit.item.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.entity.CommentEntity;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    /**
     * for getAll Items. grouping By Map
     *
     * @param itemIds Item ID
     * @return Comments
     */
    @Query("select c from CommentEntity c where c.item.id in ?1 order by c.created DESC")
    List<CommentEntity> findByItem_IdInOrderByCreatedDesc(List<Long> itemIds);

    /**
     * for get Item
     *
     * @param itemId Item ID
     * @return Comments
     */
    @Query("select c from CommentEntity c where c.item.id = ?1 order by c.created DESC")
    List<CommentEntity> findAllByItem_IdOrderByCreatedDesc(Long itemId);
}
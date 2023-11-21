package ru.practicum.shareit.item.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.CommentEntity;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> getByItem_IdIn(List<Integer> ids);
}
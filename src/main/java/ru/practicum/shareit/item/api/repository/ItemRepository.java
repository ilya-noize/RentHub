package ru.practicum.shareit.item.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item,Integer> {
    boolean existsByIdAndUserId(int id, Integer userId);
}

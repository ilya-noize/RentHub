package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    @Query("select i from ItemRequest i where i.requester.id = ?1")
    List<ItemRequest> findByRequesterId(Integer requesterId);

    @Query("select i from ItemRequest i where i.requester.id <> ?1")
    List<ItemRequest> findByRequesterIdNot(Integer requesterId);//, Pageable pageable);
}
package ru.practicum.shareit.item.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrueOrderByIdAsc(String name, String description);

    List<Item> findDistinctByOwner_IdAndAvailableTrueOrderByIdAsc(@NonNull Integer id);

    boolean existsByIdAndOwner_Id(int id, Integer id1);

    void deleteByIdAndOwner(int id, User owner);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Item i set i.name = :name, i.description = :description where i.id = :id")
    void updateNameAndDescriptionById(
            @Param("name") String name,
            @Param("description") String description,
            @Param("id") int id);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Item i set i.name = :name, i.available = :available where i.id = :id")
    void updateNameAndAvailableById(
            @Param("name") String name,
            @Param("available") boolean available,
            @Param("id") int id);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Item i set i.description = :description, i.available = :available where i.id = :id")
    void updateDescriptionAndAvailableById(
            @Param("description") String description,
            @Param("available") boolean available,
            @Param("id") int id);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Item i set i.name = :name where i.id = :id")
    void updateNameById(
            @Param("name") String name,
            @Param("id") int id);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Item i set i.description = :description where i.id = :id")
    void updateDescriptionById(
            @Param("description") String description,
            @Param("id") int id);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Item i set i.available = :available where i.id = :id")
    void updateAvailableById(
            @Param("available") boolean available,
            @Param("id") int id);
}

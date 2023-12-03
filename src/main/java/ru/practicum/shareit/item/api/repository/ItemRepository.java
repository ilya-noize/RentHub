package ru.practicum.shareit.item.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("select i from Item i where i.owner.id = ?1")
    List<Item> findAllByOwner_Id(Integer id);

    @Query("select i from Item i " +
            "where ( " +
            "upper(i.name) like upper(concat('%', :search, '%')) or upper(i.description) like upper(concat('%', :search, '%')) " +
            ") and i.available = true " +
            "order by i.id")
    List<Item> searchItemByNameOrDescription(
            @Param("search") String text);

    @Query("select (count(i) == 0) from Item i where i.id = ?1 and i.owner.id = ?2")
    boolean notExistsByIdAndOwner_Id(int itemId, Integer ownerId);

    @Transactional
    @Modifying
    @Query("delete from Item i where i.id = ?1 and i.owner.id = ?2")
    void deleteByIdAndOwner_Id(Integer itemId, Integer ownerId);

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

package ru.practicum.shareit.booking.api.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // - - - - - - - - - - - - - - - - - - ALL OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start DESC")
    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Integer id);

    // - - - - - - - - - - - - - - - - - - ALL BOOKER
    @Query("select b from Booking b where b.booker.id = ?1 order by b.start DESC")
    List<Booking> findAllByBooker_IdOrderByStartDesc(Integer id);

    // - - - - - - - - - - - - - - - - - - APPROVED, WAITING OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(
            Integer id,
            BookingStatus status);

    // - - - - - - - - - - - - - - - - - - APPROVED, WAITING BOOKER
    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(
            Integer id,
            BookingStatus status);

    // - - - - - - - - - - - - - - - - - - PAST OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
            Integer ownerId,
            LocalDateTime end);

    // - - - - - - - - - - - - - - - - - - PAST BOOKER
    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(
            Integer bookerId,
            LocalDateTime now);

    // - - - - - - - - - - - - - - - - - - FUTURE BOOKER
    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(
            Integer id,
            LocalDateTime start);

    // - - - - - - - - - - - - - - - - - - FUTURE OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
            Integer id,
            LocalDateTime start);

    // - - - - - - - - - - - - - - - - - - CURRENT BOOKER
    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?3 order by b.start DESC")
    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort);

    // - - - - - - - - - - - - - - - - - - CURRENT OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?3")
    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(
            Integer ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort);

    // - - - - - - - - - - - - - - - - - - UPDATE STATUS RIGHT NOW
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Booking b set b.status = :status where b.id = :id")
    void updateStatusById(
            @Param(value = "status") BookingStatus status,
            @Param(value = "id") @NonNull Long id);

    @Query("select b from Booking b where b.item.id = ?1 order by b.start DESC")
    List<Booking> findByItemIdOrderByStartDesc(Integer itemId);

    @Query("select b from Booking b where b.item.id = ?1 and b.booker.id = ?2 and b.status = ?3 order by b.start DESC")
    Optional<List<Booking>> findAllByItemIdAndBookerIdAndStatus(Integer itemId, Integer bookerId, BookingStatus status);
}

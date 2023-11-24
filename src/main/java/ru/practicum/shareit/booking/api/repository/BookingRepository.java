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
    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(int id);

    // - - - - - - - - - - - - - - - - - - ALL BOOKER
    List<Booking> findAllByBooker_IdOrderByStartDesc(int id);

    // - - - - - - - - - - - - - - - - - - APPROVED, WAITING OWNER
    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(
            int id,
            BookingStatus status);

    // - - - - - - - - - - - - - - - - - - APPROVED, WAITING BOOKER
    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(
            int id,
            BookingStatus status);

    // - - - - - - - - - - - - - - - - - - PAST OWNER
    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
            int ownerId,
            LocalDateTime end);

    // - - - - - - - - - - - - - - - - - - PAST BOOKER
    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(
            Integer bookerId,
            LocalDateTime now);

    // - - - - - - - - - - - - - - - - - - FUTURE BOOKER
    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(
            int id,
            LocalDateTime start);

    // - - - - - - - - - - - - - - - - - - FUTURE OWNER
    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
            int id,
            LocalDateTime start);

    // - - - - - - - - - - - - - - - - - - CURRENT BOOKER
    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            int bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort);

    // - - - - - - - - - - - - - - - - - - CURRENT OWNER
    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(
            int ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort);

    // - - - - - - - - - - - - - - - - - - NEXT

    Optional<Booking> getFirstByItem_IdAndStartGreaterThanEqualAndStatusOrderByIdDesc(
            int itemId,
            LocalDateTime start,
            BookingStatus status);

    // - - - - - - - - - - - - - - - - - - LAST
    Optional<Booking> getFirstByItem_IdAndStartAfterAndStatusOrderByIdAsc(
            int itemId,
            LocalDateTime start,
            BookingStatus status);

    // - - - - - - - - - - - - - - - - - - BOOKING IS EXISTS
    boolean existsByBooker_IdAndItem_IdAndEndLessThanAndStatus(
            int bookerId,
            int itemId,
            LocalDateTime end,
            BookingStatus status);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Booking b set b.status = :status where b.id = :id")
    void updateStatusById(
            @Param(value = "status") BookingStatus status,
            @Param(value = "id")@NonNull Long id);
}

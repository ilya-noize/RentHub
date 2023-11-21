package ru.practicum.shareit.booking.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker = :booker and b.end < :end" +
            " order by b.start DESC")
    List<Booking> findByBookerAndEndBefore(@Param("booker") User booker, @Param("end") LocalDateTime end);

    List<Booking> getAllByBooker_IdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status);

    List<Booking> getAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Integer bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> getAllByBooker_IdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime start);

    List<Booking> getAllByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime end);

    List<Booking> getAllByBooker_IdOrderByStartDesc(Integer bookerId);

    List<Booking> getAllByItem_Owner_IdAndStatusOrderByStartDesc(Integer ownerId, BookingStatus status);

    List<Booking> getAllByItem_Owner_IdAndStartBeforeOrderByStartDesc(Integer ownerId, LocalDateTime start);

    List<Booking> getAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime start);

    List<Booking> getAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end);

    List<Booking> getAllByItem_Owner_IdOrderByStartDesc(Integer ownerId);

    @Query("select b from Booking b " +
            "where b.item.id = :id and b.start < :start and b.status = :status " +
            "order by b.start DESC")
    Optional<Booking> findByItem_IdAndStartBeforeAndStatusOrderByStartDesc(@Param("id") int id, @Param("start") LocalDateTime start, @Param("status") BookingStatus status);

    @Query("select b from Booking b " +
            "where b.item.id = :id and b.start > :start and b.status = :status " +
            "order by b.start DESC")
    Optional<Booking> findByItem_IdAndStartAfterAndStatusOrderByStartDesc(@Param("id") int id, @Param("start") LocalDateTime start, @Param("status") BookingStatus status);

    Optional<Booking> getFirstByItem_IdAndStartGreaterThanEqualAndStatusOrderByIdDesc(Integer itemId, LocalDateTime start, BookingStatus status);

    Optional<Booking> getFirstByItem_IdAndStartAfterAndStatusOrderByIdAsc(Integer itemId, LocalDateTime start, BookingStatus status);

    boolean existsByBooker_IdAndItem_IdAndEndBeforeAndStatus(Integer bookerId, int itemId, LocalDateTime end, BookingStatus status);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Booking b set b.status = ?1 where b.id = ?2")
    void updateStatusById(BookingStatus status, @NonNull Long id);
}

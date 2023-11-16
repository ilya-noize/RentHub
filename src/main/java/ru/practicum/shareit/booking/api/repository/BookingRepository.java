package ru.practicum.shareit.booking.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Booking b set b.status = ?1 where b.id = ?2")
    void updateStatusById(BookingStatus status, @NonNull Long id);

    List<Booking> findAllByBookerOrderByStartDesc(User user);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(
            User booker,
            BookingStatus bookingStatus);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
            User booker,
            LocalDateTime startTimestampBefore,
            LocalDateTime endTimestampAfter);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(
            User booker,
            LocalDateTime startTimestampAfter);

    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(
            User booker,
            LocalDateTime endTimestampBefore);

    List<Booking> findAllByItem_OwnerOrderByStartDesc(User user);

    List<Booking> findAllByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
            User user,
            LocalDateTime startTimestampBefore,
            LocalDateTime endTimestampAfter);

    List<Booking> findAllByItem_OwnerAndStartAfterOrderByStartDesc(
            User user,
            LocalDateTime startTimestampAfter);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
            User owner,
            LocalDateTime endTimestampBefore);

    List<Booking> findAllByItem_OwnerAndStatusOrderByStartDesc(
            User owner,
            BookingStatus bookingStatus);

    boolean existsByItem_IdAndBooker_Id(
            Integer itemId,
            Integer bookerId);
}

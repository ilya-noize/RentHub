package ru.practicum.shareit.booking.api.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Transactional
    @Modifying
    @Query("update Booking b set b.status = ?1 where b.id = ?2")
    void updateStatusById(BookingStatus status, @NonNull Integer id);

    List<Booking> findAllByBooker_Id(Integer bookerId, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(Integer bookerId, BookingStatus bookingStatus, Sort sort);

    List<Booking> findAllByBooker_IdAndStart_TimestampBeforeAndEnd_TimestampAfter(
            Integer bookerId,
            LocalDateTime startTimestampBefore,
            LocalDateTime endTimestampAfter,
            Sort sort);

    List<Booking> findAllByBooker_IdAndStart_TimestampAfter(Integer bookerId,
                                                            LocalDateTime startTimestampAfter,
                                                            Sort sort);

    List<Booking> findAllByBooker_IdAndEnd_TimestampBefore(Integer bookerId,
                                                           LocalDateTime endTimestampBefore,
                                                           Sort sort);

    List<Booking> findAllByItemOwner_Id(Integer ownerId, Sort sort);

    List<Booking> findAllByItemOwner_IdAndStatus(Integer bookerId, BookingStatus bookingStatus, Sort sort);

    List<Booking> findAllByItemOwner_IdAndStart_TimestampBeforeAndEnd_TimestampAfter(
            Integer bookerId,
            LocalDateTime startTimestampBefore,
            LocalDateTime endTimestampAfter,
            Sort sort);

    List<Booking> findAllByItemOwner_IdAndStart_TimestampAfter(Integer bookerId,
                                                            LocalDateTime startTimestampAfter,
                                                            Sort sort);

    List<Booking> findAllByItemOwner_IdAndEnd_TimestampBefore(Integer bookerId,
                                                           LocalDateTime endTimestampBefore,
                                                           Sort sort);

    Optional<Booking> findByItem_IdAndBooker_Id(int itemId, Integer bookerId);
}

package ru.practicum.shareit.booking.api.repository;

import org.springframework.data.domain.Pageable;
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
    /**
     * - - - - - - - - - - - - - - - - - - GET ALL LAST
     *
     * @param ids    Item IDs
     * @param start  begin Booking
     * @param status finish Booking
     * @param sort   Order by column
     * @return Bookings
     */
    List<Booking> findByItem_IdInAndStartAfterAndStatus(
            List<Integer> ids,
            LocalDateTime start,
            BookingStatus status,
            Sort sort);

    /**
     * - - - - - - - - - - - - - - - - - - GET ALL NEXT
     *
     * @param ids    Item IDs
     * @param start  begin Booking
     * @param status finish Booking
     * @param sort   Order by column
     * @return Bookings
     */
    List<Booking> findByItem_IdInAndStartLessThanEqualAndStatus(
            List<Integer> ids,
            LocalDateTime start,
            BookingStatus status,
            Sort sort);

    /**
     * - - - - - - - - - - - - - - - - - - GET LAST
     *
     * @param itemId       Item IDs
     * @param now          begin Booking
     * @param approved     finish Booking
     * @param sortStartAsc Order by column
     * @return Bookings
     */
    Optional<Booking> findFirstByItem_IdAndStartAfterAndStatus(
            Integer itemId,
            LocalDateTime now,
            BookingStatus approved,
            Sort sortStartAsc);

    /**
     * - - - - - - - - - - - - - - - - - - GET NEXT
     *
     * @param itemId        Item IDs
     * @param now           begin Booking
     * @param approved      finish Booking
     * @param sortStartDesc Order by column
     * @return Bookings
     */
    Optional<Booking> findFirstByItem_IdAndStartLessThanEqualAndStatus(
            Integer itemId,
            LocalDateTime now,
            BookingStatus approved,
            Sort sortStartDesc);

    /**
     * - - - - - - - - - - - - - - - - - - ALL OWNER
     *
     * @param id       Owner ID
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start DESC")
    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Integer id,
                                                         Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - ALL BOOKER
     *
     * @param id       Booker ID
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.booker.id = ?1 order by b.start DESC")
    List<Booking> findAllByBooker_IdOrderByStartDesc(Integer id,
                                                     Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - APPROVED, WAITING OWNER
     *
     * @param id       Owner ID
     * @param status   Booking status
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(
            Integer id,
            BookingStatus status,
            Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - APPROVED, WAITING BOOKER
     *
     * @param id       Booker ID
     * @param status   Booking status
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(
            Integer id,
            BookingStatus status,
            Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - PAST OWNER
     *
     * @param ownerId  Owner ID
     * @param end      Booking end
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
            Integer ownerId,
            LocalDateTime end,
            Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - PAST BOOKER
     *
     * @param bookerId Booker ID
     * @param now      Booking end
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(
            Integer bookerId,
            LocalDateTime now,
            Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - FUTURE BOOKER
     *
     * @param id       Booker ID
     * @param start    Booking start
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(
            Integer id,
            LocalDateTime start,
            Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - FUTURE OWNER
     *
     * @param id       Owner ID
     * @param start    Booking start
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
            Integer id,
            LocalDateTime start,
            Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - CURRENT BOOKER
     *
     * @param bookerId Booker ID
     * @param start    Booking start
     * @param end      Booking end
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?3 order by b.start DESC")
    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - CURRENT OWNER
     *
     * @param ownerId  Owner ID
     * @param start    Booking start
     * @param end      Booking end
     * @param pageable Pageable
     * @return Bookings
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?3")
    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterByStartDesc(
            Integer ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);

    /**
     * - - - - - - - - - - - - - - - - - - UPDATE STATUS RIGHT NOW
     *
     * @param status Booking status
     * @param id     Booking ID
     */
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Booking b set b.status = :status where b.id = :id")
    void updateStatusById(
            @Param(value = "status") BookingStatus status,
            @Param(value = "id") @NonNull Long id);

    /**
     * - - - - - - - - - - - - - - - - - - CHECK BOOKING FOR CREATE COMMENT
     *
     * @param itemId   Item ID
     * @param bookerId Booker ID
     * @param status   Booking Status
     * @param end      finished Booking
     * @return true/false
     */
    @Query("select (count(b) > 0) from Booking b " +
            "where b.item.id = :itemId and b.booker.id = :bookerId and b.status = :status and b.end <= :end")
    boolean existsCompletedBookingByTheUserOfTheItem(
            @Param("itemId") int itemId,
            @Param("bookerId") Integer bookerId,
            @Param("status") BookingStatus status,
            @Param("end") LocalDateTime end);
}

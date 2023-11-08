package ru.practicum.shareit.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "BOOKINGS", schema = "PUBLIC")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(name = "START_TIMESTAMP")
    private LocalDateTime start;

    @Column(name = "END_TIMESTAMP")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "BOOKER_ID", referencedColumnName = "ID")
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}

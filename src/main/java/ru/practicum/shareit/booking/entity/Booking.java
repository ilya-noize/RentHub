package ru.practicum.shareit.booking.entity;

import lombok.*;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "bookings", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "START_TIME")
    private LocalDateTime start;

    @Column(name = "BREAK_TIME")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID",
            referencedColumnName = "ID")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "BOOKER_ID",
            referencedColumnName = "ID")
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            length = 16)
    private BookingStatus status;
}

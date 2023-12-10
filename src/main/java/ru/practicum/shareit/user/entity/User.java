package ru.practicum.shareit.user.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Class USER
 * <p>
 * {@code id} ID user <br/>
 * {@code name} User's name <br/>
 * {@code email} User's email <br/>
 */

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String email;
    private String name;
}

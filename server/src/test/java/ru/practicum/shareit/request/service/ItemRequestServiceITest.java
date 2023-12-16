package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.constants.Constants.LOG_SEPARATOR;


@SpringBootTest
@Transactional
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@Disabled
class ItemRequestServiceITest {
    User owner;
    long ownerId;
    User requester;
    long requesterId;
    String wanted = "PlayStation 5 на две недели есть у кого-нибудь?";
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        System.out.println(LOG_SEPARATOR);
        User u1 = User.builder()
                .name("owner")
                .email("owner@owner.com").build();
        User u2 = User.builder()
                .name("requester")
                .email("requester@requester.com").build();
        owner = userRepository.save(u1);
        requester = userRepository.save(u2);
        ownerId = owner.getId();
        requesterId = requester.getId();
    }

    @Test
    @DirtiesContext
    @DisplayName("ITEM REQUEST - CREATE")
    void create() {
        ItemRequestSimpleDto request = ItemRequestSimpleDto.builder().description(wanted).build();

        ItemRequestDto createResponse = itemRequestService.create(ownerId, request, LocalDateTime.now());

        assertEquals(1, createResponse.getId());
        assertEquals(wanted, createResponse.getDescription());

        ItemRequestDto getResponse = itemRequestService.get(requesterId, createResponse.getId());

        System.out.println("createResponse = " + createResponse);
        System.out.println("getResponse = " + getResponse);
    }
}

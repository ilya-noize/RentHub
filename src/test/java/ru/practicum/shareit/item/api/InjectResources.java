package ru.practicum.shareit.item.api;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

import static ru.practicum.shareit.utils.ResourcePool.*;

@Setter
@Getter
public class InjectResources {
    protected List<User> users;
    protected List<Item> items;
    protected List<UserDto> userDtoList;

    protected User userRequest;
    protected User userResponse;

    protected Item itemRequest;
    protected Item itemResponse;

    protected static UserSimpleDto userDtoRequest;
    protected UserDto userDtoResponse;

    protected static ItemSimpleDto itemDtoRequest;
    protected ItemDto itemDtoResponse;

    protected List<ItemSimpleDto> itemDtoList;


    @BeforeEach
    void setUp() {
        users = readResource(CREATE_USER_ENTITIES, new TypeReference<>() {
        });
        items = readResource(CREATE_ITEM_ENTITIES, new TypeReference<>() {
        });
        userDtoList = readResource(CREATED_USER_DTO_S, new TypeReference<>() {
        });

        userRequest = readResource(CREATE_USER_ENTITY_REQUEST, User.class);
        userResponse = readResource(CREATE_USER_ENTITY_RESPONSE, User.class);

        itemRequest = readResource(CREATE_ITEM_ENTITY_REQUEST, Item.class);
        itemResponse = readResource(CREATE_ITEM_ENTITY_RESPONSE, Item.class);

        userDtoRequest = readResource(CREATED_USER_DTO_REQUEST, UserSimpleDto.class);
        userDtoResponse = readResource(CREATED_USER_DTO_RESPONSE, UserDto.class);


        itemDtoRequest = ItemSimpleDto.builder()
                .id(itemRequest.getId())
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.isAvailable()).build();
        itemDtoResponse = ItemDto.builder()
                .id(1)
                .name(itemResponse.getName())
                .description(itemResponse.getDescription())
                .available(itemResponse.isAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(null).build();

        itemDtoList = List.of(
                ItemSimpleDto.builder()
                        .id(1)
                        .name("Стол")
                        .description("Размер: метр на 60. Высота 75.")
                        .available(true)
                        .build(),
                ItemSimpleDto.builder()
                        .id(2)
                        .name("Столовые приборы")
                        .description("Чайные, десертные, столовые ложки. Ножи. Вилки. На 6 персон.")
                        .available(true)
                        .build(),
                ItemSimpleDto.builder()
                        .id(3)
                        .name("Шуруповёрт")
                        .description("Работает от сети 220 Вольт. Кейс в комплекте.")
                        .available(true)
                        .build(),
                ItemSimpleDto.builder()
                        .id(4)
                        .name("Гайковёрт")
                        .description("В комплекте 2 аккумулятора, зарядка к ним и 3 головки на 12, 14, 16мм")
                        .available(true)
                        .build(),
                ItemSimpleDto.builder()
                        .id(5)
                        .name("Разводные ключи")
                        .description("Для сантехнических/газовых работ")
                        .available(true)
                        .build(),
                ItemSimpleDto.builder()
                        .id(6)
                        .name("Набор гаечных ключей")
                        .description("Размеры от 6мм до 20мм с шагом в мм")
                        .available(true)
                        .build(),
                ItemSimpleDto.builder()
                        .id(7)
                        .name("Стол")
                        .description("Раскладной стол. В разложенном виде размеры 1,6м Х 0,8м")
                        .available(true)
                        .build(),
                ItemSimpleDto.builder()
                        .id(8)
                        .name("Стремянка")
                        .description("3 ступеньки + площадка. Алюминиевая. Компактная")
                        .available(true)
                        .build(),
                ItemSimpleDto.builder()
                        .id(9)
                        .name("Компьютер")
                        .description("Athlon X2 250. DDR3 8Gb. Для интернета сойдёт.")
                        .available(true)
                        .build()
        );
    }
}

package ru.practicum.shareit.item.api;

import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.entity.Item;

import static ru.practicum.shareit.utils.ResourcePool.*;

@Setter
public class ItemInjectResources {
    protected ItemSimpleDto itemDtoRequest;
    protected ItemDto itemDtoResponse;
    protected Item itemRequest;
    protected Item itemResponse;

    @BeforeEach
    void setUp() {
//    @Setter
//    private List<ItemDto> itemDtoList;
//    @Setter
//    private List<Item> items;
//        this.setItemDtoList(
//                readResource(CREATED_ITEM_DTO_S,
//                        new TypeReference<List<ItemDto>>() {
//                        }));
//        this.setItems(readResource(
//                CREATE_ITEM_ENTITIES, new TypeReference<>() {
//                }));
//        Parsing error
//        this.setItemDtoRequest(readResource(CREATED_ITEM_DTO_REQUEST, ItemSimpleDto.class));
//        this.setItemDtoResponse(readResource(CREATED_ITEM_DTO_RESPONSE, ItemDto.class));
        this.setItemRequest(readResource(CREATE_ITEM_ENTITY_REQUEST, Item.class));
        this.setItemResponse(readResource(CREATE_ITEM_ENTITY_RESPONSE, Item.class));
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
    }
}

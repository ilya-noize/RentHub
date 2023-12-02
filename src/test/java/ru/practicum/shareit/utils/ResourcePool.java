package ru.practicum.shareit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
public final class ResourcePool {

    public static final String ERROR_IO = "Ошибка при получении данных из файла-ресурса";

    // for mapping List<Object>
    public static final Resource CREATE_USER_ENTITIES =
            new ClassPathResource("json/entities/user/users.json");
    public static final Resource CREATED_USER_DTO_S =
            new ClassPathResource("json/dtos/user/users.json");
    public static final Resource CREATE_ITEM_ENTITIES =
            new ClassPathResource("json/entities/item/items.json");
    public static final Resource CREATED_ITEM_DTO_S =
            new ClassPathResource("json/dtos/item/items.json");

    // for mapping Object
    public static final Resource CREATED_USER_DTO_REQUEST =
            new ClassPathResource("json/dto/user/user-request.json");
    public static final Resource CREATED_USER_DTO_RESPONSE =
            new ClassPathResource("json/dto/user/user-response.json");

    public static final Resource CREATED_ITEM_DTO_REQUEST =
            new ClassPathResource("json/dto/item/item-request.json");
    public static final Resource CREATED_ITEM_DTO_RESPONSE =
            new ClassPathResource("json/dto/item/item-response.json");
    public static final Resource CREATE_USER_ENTITY_RESPONSE =
            new ClassPathResource("json/entity/user/user-response.json");
    public static final Resource CREATE_USER_ENTITY_REQUEST =
            new ClassPathResource("json/entity/user/user-request.json");

    public static final Resource CREATE_ITEM_ENTITY_RESPONSE =
            new ClassPathResource("json/entity/item/item-response.json");
    public static final Resource CREATE_ITEM_ENTITY_REQUEST =
            new ClassPathResource("json/entity/item/item-request.json");
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> T readResource(Resource resource, Class<T> objectClass) {
        try {
            return mapper.readValue(resource.getInputStream(), objectClass);
        } catch (IOException e) {
            log.error(ERROR_IO, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T readResource(Resource resource, TypeReference<T> tr) {
        try {
            return mapper.readValue(resource.getInputStream(), tr);
        } catch (IOException e) {
            log.error(ERROR_IO, e);
            throw new RuntimeException(e);
        }
    }
}
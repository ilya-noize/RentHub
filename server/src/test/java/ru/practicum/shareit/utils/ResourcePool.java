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
    public static final Resource CREATE_ITEM_ENTITIES =
            new ClassPathResource("json/entities/item/items.json");
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> T readResource(Resource resource, TypeReference<T> tr) {
        try {
            return mapper.readValue(resource.getInputStream(), tr);
        } catch (IOException e) {
            log.error(ERROR_IO, e);
            throw new RuntimeException(e);
        }
    }
}
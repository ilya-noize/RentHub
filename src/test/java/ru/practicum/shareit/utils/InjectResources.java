package ru.practicum.shareit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static ru.practicum.shareit.ShareItApp.LOGGING_IN_TEST;
import static ru.practicum.shareit.utils.ResourcePool.*;

/**
 * OwnerStorage by UserId:[1]
 * ItemId:[1]	Item(id = 1, name = Стол, description = Размер: метр на 60. Высота 75., available = true)
 * ItemId:[4]	Item(id = 4, name = Гайковёрт, description = В комплекте 2 аккумулятора, зарядка к ним и 3 головки на 12, 14, 16мм, available = true)
 * ItemId:[7]	Item(id = 7, name = Стол, description = Раскладной стол. В разложенном виде размеры 1,6м Х 0,8м, available = true)
 * OwnerStorage by UserId:[2]
 * ItemId:[2]	Item(id = 2, name = Столовые приборы, description = Чайные, десертные, столовые ложки. Ножи. Вилки. На 6 персон., available = true)
 * ItemId:[5]	Item(id = 5, name = Разводные ключи, description = Для сантехнических/газовых работ, available = true)
 * ItemId:[8]	Item(id = 8, name = Стремянка, description = 3 ступеньки + площадка. Алюминиевая. Компактная, available = true)
 * OwnerStorage by UserId:[3]
 * ItemId:[3]	Item(id = 3, name = Шуруповёрт, description = Работает от сети 220 Вольт. Кейс в комплекте., available = true)
 * ItemId:[6]	Item(id = 6, name = Набор гаечных ключей, description = Размеры от 6мм до 20мм с шагом в мм, available = true)
 * ItemId:[9]	Item(id = 9, name = Компьютер, description = Athlon X2 250. DDR3 8Gb. Для интернета сойдёт., available = true)
 */
@Getter
public class InjectResources {
    protected final EasyRandom random = new EasyRandom();

    protected final LocalDateTime now = LocalDateTime
            // created at 2000 Jan, 1, PM12:00:00.0000
            .of(2000, 1, 1, 12, 0, 0, 0);
    public Map<Integer, User> userStorage;
    public Map<Integer, Item> itemStorage;
    public Map<User, List<Item>> ownerStorage;
    protected List<Item> items = readResource(CREATE_ITEM_ENTITIES, new TypeReference<>() {
    });
    protected List<User> users = readResource(CREATE_USER_ENTITIES, new TypeReference<>() {
    });

    @BeforeEach
    void createsEnvironmentObjects() {
        items = readResource(CREATE_ITEM_ENTITIES, new TypeReference<>() {
        });
        users = readResource(CREATE_USER_ENTITIES, new TypeReference<>() {
        });

        if (items.isEmpty()) {
            throw new NullPointerException("NO ITEMS");
        }
        if (users.isEmpty()) {
            throw new NullPointerException("NO USERS");
        }

        items.forEach(item -> {
            int itemId = item.getId();
            if (itemId % 3 == 1) {
                item.setOwner(users.get(0));
            } else if (itemId % 3 == 2) {
                item.setOwner(users.get(1));
            } else {
                item.setOwner(users.get(2));
            }
        });

        userStorage = this.users.stream()
                .collect(toMap(User::getId,
                        Function.identity(),
                        (first, second) -> first));
        itemStorage = this.items.stream()
                .collect(toMap(Item::getId,
                        Function.identity(),
                        (first, second) -> first));
        ownerStorage = this.items.stream()
                .collect(Collectors.groupingBy(Item::getOwner));

        if (LOGGING_IN_TEST) {
            System.out.println("- ".repeat(40));
            for (User user : ownerStorage.keySet()) {
                System.out.printf("OwnerStorage by UserId:[%d]%n", user.getId());
                for (Item item : ownerStorage.get(user)) {
                    System.out.printf("\tItemId:[%d]\t%s%n", item.getId(), item);
                }
            }
        }
    }
}

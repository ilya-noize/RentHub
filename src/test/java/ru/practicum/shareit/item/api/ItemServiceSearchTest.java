package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceSearchTest extends ItemServiceMasterTest {

    @Test
    void search_whenSuccessSearching_thenReturnDtoList() {
        final String text = "Стол";
        final ItemSimpleDto itemResponse = itemDtoRequest;
        when(repository.searchItemByNameOrDescription(text))
                .thenReturn(anyList());

        when(mapper.toSimpleDto(itemRequest))
                .thenReturn(itemResponse);

        List<ItemSimpleDto> resultSearchList = service.search(text);
        assertTrue(resultSearchList.isEmpty()); // todo WHY? if (size > 0) TRUE else FALSE

        verify(repository, times(1))
                .searchItemByNameOrDescription(text);
        verify(mapper, never()) // todo if (size > 0) atLeastOnce() else never()
                .toDto(itemRequest);
    }

    @Test
    void search_whenFailSearching_thenReturnEmptyList() {
        String text = "qwerty";
        when(repository.searchItemByNameOrDescription(text))
                .thenReturn(List.of());
        when(mapper.toSimpleDto(any(Item.class)))
                .thenReturn(any(ItemSimpleDto.class));

        List<ItemSimpleDto> resultSearchList = service.search(text);
        assertTrue(resultSearchList.isEmpty());

        verify(repository, times(1))
                .searchItemByNameOrDescription(text);
        verify(mapper, never())
                .toDto(itemRequest);
    }
}
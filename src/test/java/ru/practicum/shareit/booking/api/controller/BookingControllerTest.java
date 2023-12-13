package ru.practicum.shareit.booking.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingMapper;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.api.service.BookingService;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.RentalPeriodException;
import ru.practicum.shareit.exception.StateException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.HEADER_USER_ID;
import static ru.practicum.shareit.constants.Constants.RANDOM;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
class BookingControllerTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final int bookerId = 1;
    private final BookingSimpleDto bookingSimpleDto = RANDOM.nextObject(BookingSimpleDto.class);
    private Booking booking;
    private BookingDto bookingDto;
    private List<BookingDto> bookingDtoList;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        bookingSimpleDto.setStart(now.plusDays(1));
        bookingSimpleDto.setEnd(now.plusDays(2));
        booking = BookingMapper.INSTANCE.toEntity(bookingSimpleDto, bookerId);
        bookingDto = BookingMapper.INSTANCE.toDto(booking);
        bookingDtoList = List.of(bookingDto);
    }

    @Test
    @DisplayName("CREATE_BOOKING:" + Constants.CREATE_BOOKING)
    void create() throws Exception {

        when(bookingService.create(anyInt(), any(BookingSimpleDto.class)))
                .thenReturn(bookingDto);

        mvc.perform(post(Constants.CREATE_BOOKING)
                        .header(HEADER_USER_ID, bookerId)
                        .content(mapper.writeValueAsString(bookingSimpleDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingSimpleDto.getId()));

        verify(bookingService, times(1))
                .create(anyInt(), any(BookingSimpleDto.class));
    }

    @Test
    @DisplayName("CREATE_BOOKING: " + Constants.CREATE_BOOKING + " It is impossible to rent an item to which access is closed.")
    void create_ItemNotAvailable_Throw() throws Exception {

        when(bookingService.create(anyInt(), any(BookingSimpleDto.class)))
                .thenThrow(BadRequestException.class);

        mvc.perform(post(Constants.CREATE_BOOKING)
                        .header(HEADER_USER_ID, bookerId)
                        .content(mapper.writeValueAsString(bookingSimpleDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verify(bookingService, times(1))
                .create(anyInt(), any(BookingSimpleDto.class));
    }

    @Test
    @DisplayName("CREATE_BOOKING: " + Constants.CREATE_BOOKING + " Access denied. You are owner this item")
    void create_accessDenied_Throw() throws Exception {

        when(bookingService.create(anyInt(), any(BookingSimpleDto.class)))
                .thenThrow(BookingException.class);

        mvc.perform(post(Constants.CREATE_BOOKING)
                        .header(HEADER_USER_ID, bookerId)
                        .content(mapper.writeValueAsString(bookingSimpleDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        verify(bookingService, times(1))
                .create(anyInt(), any(BookingSimpleDto.class));
    }


    @Test
    @DisplayName("CREATE_BOOKING: " + Constants.CREATE_BOOKING + "The effective date of the lease agreement"
            + " coincides with its termination OR after its termination")
    void create_rentalPeriod_Throw() throws Exception {

        when(bookingService.create(anyInt(), any(BookingSimpleDto.class)))
                .thenThrow(RentalPeriodException.class);

        mvc.perform(post(Constants.CREATE_BOOKING)
                        .header(HEADER_USER_ID, bookerId)
                        .content(mapper.writeValueAsString(bookingSimpleDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verify(bookingService, times(1))
                .create(anyInt(), any(BookingSimpleDto.class));
    }

    @Test
    @DisplayName("UPDATE_STATUS_BOOKING:" + Constants.UPDATE_STATUS_BOOKING)
    void update() throws Exception {
        when(bookingService.update(anyInt(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch(Constants.UPDATE_STATUS_BOOKING, booking.getId())
                        .header(HEADER_USER_ID, bookerId)
                        .content(mapper.writeValueAsString(bookingSimpleDto))
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingSimpleDto.getId()));

        verify(bookingService, times(1))
                .update(anyInt(), anyLong(), Mockito.anyBoolean());
    }

    @Test
    @DisplayName("GET_BOOKING:" + Constants.GET_BOOKING)
    void getBookingById() throws Exception {
        when(bookingService.get(anyInt(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get(Constants.GET_BOOKING, booking.getId())
                        .header(HEADER_USER_ID, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingSimpleDto.getId()));

        verify(bookingService, times(1))
                .get(anyInt(), anyLong());
    }

    @Test
    @DisplayName("GET_ALL_BOOKINGS_FOR_USER:" + Constants.GET_ALL_BOOKINGS_FOR_USER)
    void getByBookerId() throws Exception {
        when(bookingService.getAllByUser(anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookingDtoList);

        mvc.perform(get(Constants.GET_ALL_BOOKINGS_FOR_USER)
                        .header(HEADER_USER_ID, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoList.get(0).getId()));

        verify(bookingService, times(1))
                .getAllByUser(anyInt(),
                        anyString(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    @DisplayName("GET_ALL_BOOKINGS_FOR_OWNER:" + Constants.GET_ALL_BOOKINGS_FOR_OWNER)
    void getAllOwner() throws Exception {
        String state = "FUTURE";

        when(bookingService.getAllByOwner(anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookingDtoList);

        mvc.perform(get(Constants.GET_ALL_BOOKINGS_FOR_OWNER)
                        .param("state", state)
                        .header(HEADER_USER_ID, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoList.get(0).getId()));

        verify(bookingService, times(1))
                .getAllByOwner(anyInt(),
                        anyString(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    @DisplayName("GET_ALL_BOOKINGS_FOR_OWNER:" + Constants.GET_ALL_BOOKINGS_FOR_OWNER + " \"Unknown state")
    void getAllOwner_wrongFilter() throws Exception {
        String state = "UNSUPPORTED_STATUS";

        when(bookingService.getAllByOwner(anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(Pageable.class)))
                .thenThrow(StateException.class);

        mvc.perform(get(Constants.GET_ALL_BOOKINGS_FOR_OWNER)
                        .param("state", state)
                        .header(HEADER_USER_ID, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(1))
                .getAllByOwner(anyInt(),
                        anyString(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }
}

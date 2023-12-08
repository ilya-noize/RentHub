package ru.practicum.shareit.booking.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingMapper;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.entity.Booking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ShareItApp.HEADER_USER_ID;
import static ru.practicum.shareit.booking.api.BookingController.*;

@WebMvcTest(controllers = BookingController.class)
@Disabled
class BookingControllerTest {
    private final EasyRandom random = new EasyRandom();
    private final LocalDateTime now = LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0);
    private final int bookerId = 1;
    private final BookingSimpleDto requestDto = random.nextObject(BookingSimpleDto.class);
    private Booking booking;
    private BookingDto expectedDto;
    private List<BookingDto> expectedDtoList;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        requestDto.setStart(now.plusDays(1));
        requestDto.setEnd(now.plusDays(2));
        booking = BookingMapper.INSTANCE.toEntity(requestDto, bookerId);
        expectedDto = BookingMapper.INSTANCE.toDto(booking);
        expectedDtoList = List.of(expectedDto);
    }

    @Test
    @DisplayName("CREATE_BOOKING:" + CREATE_BOOKING)
    void addBooking() throws Exception {
        when(bookingService.create(anyInt(), any(BookingSimpleDto.class)))
                .thenReturn(expectedDto);

        mvc.perform(post(CREATE_BOOKING)
                        .header(HEADER_USER_ID, bookerId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()));

        verify(bookingService, times(1))
                .create(anyInt(), any(BookingSimpleDto.class));
    }

    @Test
    @DisplayName("UPDATE_STATUS_BOOKING:" + UPDATE_STATUS_BOOKING)
    void approvedOrRejectedBooking() throws Exception {
        when(bookingService.update(anyInt(), anyLong(), anyBoolean()))
                .thenReturn(expectedDto);

        mvc.perform(patch(UPDATE_STATUS_BOOKING, booking.getId())
                        .header(HEADER_USER_ID, bookerId)
                        .content(mapper.writeValueAsString(requestDto))
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()));

        verify(bookingService, times(1))
                .update(anyInt(), anyLong(), Mockito.anyBoolean());
    }

    @Test
    @DisplayName("GET_BOOKING:" + GET_BOOKING)
    void getBookingById() throws Exception {
        when(bookingService.get(anyInt(), anyLong())).thenReturn(expectedDto);

        mvc.perform(get(GET_BOOKING, booking.getId())
                        .header(HEADER_USER_ID, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()));

        verify(bookingService, times(1))
                .get(anyInt(), anyLong());
    }

    @Test
    @DisplayName("GET_ALL_BOOKINGS_FOR_USER:" + GET_ALL_BOOKINGS_FOR_USER)
    void getBookingsByBookerId() throws Exception {
        when(bookingService.getAllByUser(anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(expectedDtoList);

        mvc.perform(get(GET_ALL_BOOKINGS_FOR_USER)
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedDtoList.get(0).getId()));

        verify(bookingService, times(1))
                .getAllByUser(anyInt(),
                        anyString(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    @DisplayName("GET_ALL_BOOKINGS_FOR_OWNER:" + GET_ALL_BOOKINGS_FOR_OWNER)
    void getAllBookingsForItemsByOwnerId() throws Exception {
        when(bookingService.getAllByOwner(anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(expectedDtoList);

        mvc.perform(get(GET_ALL_BOOKINGS_FOR_OWNER)
                        .header(HEADER_USER_ID, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedDtoList.get(0).getId()));

        verify(bookingService, times(1))
                .getAllByOwner(anyInt(),
                        anyString(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }
}
//    private final DateTimeFormatter formatter = ofPattern("yyyy-MM-dd HH:mm");
//    // created at 2000 year Jan, 1, PM12:00:00.000
//    private final LocalDateTime now = LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0);
//    private final int days = 7;
//    private final int rentTime = 3;
//    private final LocalDateTime start = now.plusDays(days);
//    private final LocalDateTime end = start.plusDays(rentTime);
//    private final BookingSimpleDto bookingRequest = new BookingSimpleDto(1L, start, end, 1);
//    private BookingDto bookingResponse;
//    @Autowired(required = false)
//    private BookingMapper bookingMapper;
//
//    @Autowired
//    private ObjectMapper mapper;
//    @Autowired
//    private MockMvc mvc;
//    @MockBean
//    private BookingServiceImpl bookingService;
//
//    private BookingDto getBookingResponse() {
//        Item i = itemStorage.get(1);
//        User u = userStorage.get(1);
//        return bookingResponse = new BookingDto(
//                1L, start, end,
//                BookingStatus.APPROVED,
//                new BookingDto.ItemDto(i.getId(), i.getName()),
//                new BookingDto.BookerDto(u.getId()));
////        return BookingMapper.COPY.toDtoRecord(
////                BookingMapper.COPY.toEntity(BookingSimpleDto.builder()
////                        .id(1L)
////                        .itemId(1)
////                        .start(start)
////                        .end(end).build(), 1));
//    }
//
//    @Test
//    void create() throws Exception {
//        bookingResponse = getBookingResponse();
//
//        when(bookingService.create(1, bookingRequest))
//                .thenReturn(bookingResponse);
//
//        RequestBuilder requestBuilder = post(CREATE_BOOKING)
//                .header(HEADER_USER_ID, 1)
//                .content(mapper.writeValueAsString(bookingRequest))
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON);
//
//        ResultMatcher[] resultMatchers = {
//                jsonPath("$.id").value(bookingResponse.getId()),
//                jsonPath("$.start").value(bookingResponse.getStart()),
//                jsonPath("$.end").value(bookingResponse.getEnd()),
//                jsonPath("$.status").value(bookingResponse.getStatus()),
//                jsonPath("$.booker").value(bookingResponse.getBooker()),
//                jsonPath("$.item").value(bookingResponse.getItem())
//        };
//
//        mvc.perform(requestBuilder)
//                .andExpectAll(resultMatchers)
//                .andExpect(status().isOk());
//
//        verify(bookingService, times(1))
//                .create(1, bookingRequest);
//    }
//
//    @Test
//    @DisplayName("Создание бронирования")
//    void addBooking() throws Exception {
//        BookingSimpleDto requestDto = bookingRequest;
//        requestDto.setStart(LocalDateTime.now().plusDays(1));
//        requestDto.setEnd(LocalDateTime.now().plusDays(2));
//
//        bookingResponse = getBookingResponse();
//
//        when(bookingService.create(anyInt(), any(BookingSimpleDto.class)))
//                .thenReturn(any(BookingDto.class));
//
//        mvc.perform(post("/bookings")
//                        .header(HEADER_USER_ID, 1)
//                        .content(mapper.writeValueAsString(bookingRequest))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.ALL_VALUE))
//                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
//                .andExpect(status().isOk());
//
//        verify(bookingService).create(anyInt(), any(BookingSimpleDto.class));
//    }
//
//    @Test
//    void update_APPROVED() throws Exception {
//        bookingResponse = getBookingResponse();
//        bookingResponse.setStatus(BookingStatus.APPROVED);
//        String status = "true";
//
//        when(bookingService.update(1, 1L, Boolean.valueOf(status)))
//                .thenReturn(bookingResponse);
//
//        RequestBuilder requestBuilder = patch(UPDATE_STATUS_BOOKING, 1)
//                .content(mapper.writeValueAsString(bookingRequest))
//                .header(HEADER_USER_ID, 1)
//                .param("status", status)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON);
//        ResultMatcher[] resultMatchers = {
//                jsonPath("$.id").value(bookingResponse.getId()),
//                jsonPath("$.start").value(bookingResponse.getStart()),
//                jsonPath("$.end").value(bookingResponse.getEnd()),
//                jsonPath("$.status").value(bookingResponse.getStatus()),
//                jsonPath("$.booker").value(bookingResponse.getBooker()),
//                jsonPath("$.item").value(bookingResponse.getItem())
//        };
//
//        mvc.perform(requestBuilder)
//                .andExpectAll(resultMatchers)
//                .andExpect(status().isOk());
//
//        verify(bookingService, times(1))
//                .update(1, 1L, Boolean.valueOf(status));
//    }
//
//    @Test
//    void update_REJECTED() throws Exception {
//        bookingResponse = getBookingResponse();
//        bookingResponse.setStatus(BookingStatus.REJECTED);
//        String status = "false";
//
//        when(bookingService.update(1, 1L, Boolean.valueOf(status)))
//                .thenReturn(bookingResponse);
//
//        RequestBuilder requestBuilder = patch(UPDATE_STATUS_BOOKING, 1)
//                .content(mapper.writeValueAsString(bookingRequest))
//                .header(HEADER_USER_ID, 1)
//                .param("status", status)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON);
//        ResultMatcher[] resultMatchers = {
//                jsonPath("$.id").value(bookingResponse.getId()),
//                jsonPath("$.start").value(bookingResponse.getStart()),
//                jsonPath("$.end").value(bookingResponse.getEnd()),
//                jsonPath("$.status").value(bookingResponse.getStatus()),
//                jsonPath("$.booker").value(bookingResponse.getBooker()),
//                jsonPath("$.item").value(bookingResponse.getItem())
//        };
//
//        mvc.perform(requestBuilder)
//                .andExpectAll(resultMatchers)
//                .andExpect(status().isOk());
//
//        verify(bookingService, times(1))
//                .update(1, 1L, Boolean.valueOf(status));
//    }
//
//    @Test
//    void get_whenUserIdAndBookingIdExists_thenReturnDto() throws Exception {
//        bookingResponse = getBookingResponse();
//
//        when(bookingService.get(1, 1L))
//                .thenReturn(bookingResponse);
//
//        RequestBuilder requestBuilder = get(GET_BOOKING, 1)
//                .content(mapper.writeValueAsString(bookingRequest))
//                .header(HEADER_USER_ID, 1);
//        ResultMatcher[] resultMatchers = {
//                jsonPath("$.id").value(bookingResponse.getId()),
//                jsonPath("$.start").value(bookingResponse.getStart().format(formatter)),
//                jsonPath("$.end").value(bookingResponse.getEnd().format(formatter)),
//                jsonPath("$.status").value(bookingResponse.getStatus()),
//                jsonPath("$.booker").value(bookingResponse.getBooker()),
//                jsonPath("$.item").value(bookingResponse.getItem())
//        };
//
//        mvc.perform(requestBuilder)
//                .andExpectAll(resultMatchers)
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getAllByUser() throws Exception {
//        bookingResponse = getBookingResponse();
//
//        when(bookingService.get(1, 1L))
//                .thenReturn(bookingResponse);
//
//        RequestBuilder requestBuilder = get(ALL_BOOKING_FOR_USER, 1)
//                .content(mapper.writeValueAsString(bookingRequest))
//                .header(HEADER_USER_ID, 1);
//        ResultMatcher[] resultMatchers = {
//                jsonPath("$[0].id").value(bookingResponse.getId()),
//                jsonPath("$[0].start").value(bookingResponse.getStart()),
//                jsonPath("$[0].end").value(bookingResponse.getEnd()),
//                jsonPath("$[0].status").value(bookingResponse.getStatus()),
//                jsonPath("$[0].booker").value(bookingResponse.getBooker()),
//                jsonPath("$[0].item").value(bookingResponse.getItem())
//        };
//
//        mvc.perform(requestBuilder)
//                .andExpectAll(resultMatchers)
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getAllByOwner() throws Exception {
//        bookingResponse = getBookingResponse();
//
//        when(bookingService.get(1, 1L))
//                .thenReturn(bookingResponse);
//
//        RequestBuilder requestBuilder = get(ALL_BOOKING_FOR_OWNER, 1)
//                .content(mapper.writeValueAsString(bookingRequest))
//                .header(HEADER_USER_ID, 1);
//        ResultMatcher[] resultMatchers = {
//                jsonPath("$[0].id").value(bookingResponse.getId()),
//                jsonPath("$[0].start").value(bookingResponse.getStart()),
//                jsonPath("$[0].end").value(bookingResponse.getEnd()),
//                jsonPath("$[0].status").value(bookingResponse.getStatus()),
//                jsonPath("$[0].booker").value(bookingResponse.getBooker()),
//                jsonPath("$[0].item").value(bookingResponse.getItem())
//        };
//
//        mvc.perform(requestBuilder)
//                .andExpectAll(resultMatchers)
//                .andExpect(status().isOk());
//    }
//
//    /**/
//}
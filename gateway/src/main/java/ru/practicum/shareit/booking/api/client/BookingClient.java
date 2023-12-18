package ru.practicum.shareit.booking.api.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.api.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.RentalPeriodException;
import ru.practicum.shareit.valid.ValidPageable;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> create(long userId, BookingSimpleDto bookingSimpleDto) {
        LocalDateTime start = bookingSimpleDto.getStart();
        LocalDateTime end = bookingSimpleDto.getEnd();
        if (start.equals(end)) {
            String error = "The effective date of the lease agreement"
                    + " coincides with its termination";
            throw new RentalPeriodException(error);
        }
        if (start.isAfter(end)) {
            String error = "The effective date of the lease agreement"
                    + " after its termination";
            throw new RentalPeriodException(error);
        }

        return post("", userId, bookingSimpleDto);
    }

    public ResponseEntity<Object> update(long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved);

        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByUser(long userId, String stateIn, Integer from, Integer size) {
        ValidPageable.check(from, size);
        BookingState state = BookingState.from(stateIn);
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );

        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByOwner(long userId, String stateIn, Integer from, Integer size) {
        ValidPageable.check(from, size);
        BookingState state = BookingState.from(stateIn);
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );

        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

}

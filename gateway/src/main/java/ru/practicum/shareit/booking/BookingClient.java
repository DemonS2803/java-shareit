package ru.practicum.shareit.booking;

import java.util.Map;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.web.client.BaseClient;
import ru.practicum.shareit.common.web.util.HttpConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
class BookingClient extends BaseClient {

    private String serverUrl;

    @Autowired
    public BookingClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + HttpConstants.BOOKING_API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingRequestState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                HttpConstants.BOOKING_STATE_PARAM, state.name(),
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        String path = buildParametersFromMap(parameters);
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBooking(long userId, BookingRequestState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                HttpConstants.BOOKING_STATE_PARAM, state.name(),
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        String path = "/owner" + buildParametersFromMap(parameters);
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> bookItem(long userId, CreateBookingDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> approveBooking(long userId, Long bookingId, boolean approved) {
        return patch(String.format("/%s?%s=%s", bookingId, HttpConstants.APPROVE_BOOKING_PARAM, approved), userId);
    }

}

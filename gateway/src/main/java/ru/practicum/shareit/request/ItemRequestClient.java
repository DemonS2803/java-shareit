package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.web.client.BaseClient;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    @Autowired
    public ItemRequestClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + HttpConstants.ITEM_REQUEST_API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> getItemRequestById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getUserItemRequests(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        return get(buildParametersFromMap(parameters), userId);

    }

    public ResponseEntity<Object> getOtherItemRequests(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        return get("/all" + buildParametersFromMap(parameters), userId);
    }

    public ResponseEntity<Object> createItemRequest(long userId, CreateItemRequestDto createItemRequestDto) {
        return post("", userId, createItemRequestDto);
    }

}

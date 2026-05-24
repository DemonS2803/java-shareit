package ru.practicum.shareit.item;

import java.util.Map;

import ru.practicum.shareit.common.web.client.BaseClient;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
class ItemClient extends BaseClient {

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + HttpConstants.ITEM_API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> getItem(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        return get(buildParametersFromMap(parameters), userId);
    }

    public ResponseEntity<Object> searchItemsByText(long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                HttpConstants.SEARCH_ITEM_TEXT_PARAM, text,
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        String path = buildParametersFromMap(parameters);
        return get("/search" + path, userId, parameters);
    }

    public ResponseEntity<Object> createItem(long userId, CreateItemDto createItemDto) {
        return post("", userId, createItemDto);
    }

    public ResponseEntity<Object> updateItem(long userId, Long itemId, UpdateItemDto updateItemDto) {
        return patch("/" + itemId, userId, updateItemDto);
    }

    public ResponseEntity<Object> commentItem(long userId, Long itemId, CreateCommentDto createCommentDto) {
        return post(String.format("/%s/comment", itemId), userId, createCommentDto);
    }

}

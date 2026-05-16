package ru.practicum.shareit.user;

import ru.practicum.shareit.common.web.client.BaseClient;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
class UserClient extends BaseClient {

    @Autowired
    public UserClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + HttpConstants.USER_API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> getUser(long userId) {
        return get("/" + userId, userId);
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public ResponseEntity<Object> createUser(CreateUserDto createUserDto) {
        return post("", createUserDto);
    }

    public ResponseEntity<Object> updateUser(long userId, UpdateUserDto updateUserDto) {
        return patch("/" + userId, updateUserDto);
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        return delete("/" + userId);
    }

}

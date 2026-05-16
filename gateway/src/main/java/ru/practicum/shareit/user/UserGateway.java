package ru.practicum.shareit.user;

import jakarta.validation.Valid;

import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(HttpConstants.USER_API_PREFIX)
class UserGateway {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.debug("Fetching all users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable final Long id) {
        log.debug("Fetching user with id: {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid final CreateUserDto createDto) {
        log.info("Create new user request: {}", createDto);
        return userClient.createUser(createDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid final UpdateUserDto updateDto,
                                             @PathVariable final Long id) {
        updateDto.setId(id);
        log.info("Update user request: {}", updateDto);
        return userClient.updateUser(id, updateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable final Long id) {
        log.info("Delete user request: {}", id);
        return userClient.deleteUser(id);
    }

}

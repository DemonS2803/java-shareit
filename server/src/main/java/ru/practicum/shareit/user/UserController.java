package ru.practicum.shareit.user;

import java.util.List;

import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = HttpConstants.USER_API_PREFIX)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Fetching all users");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable final Long id) {
        log.debug("Fetching user with id: {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto createUser(@RequestBody final CreateUserDto createDto) {
        log.info("Create new user request: {}", createDto);
        return userService.saveUser(createDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody final UpdateUserDto updateDto,
                              @PathVariable final Long id) {
        updateDto.setId(id);
        log.info("Update user request: {}", updateDto);
        return userService.updateUser(updateDto);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable final Long id) {
        log.info("Delete user request: {}", id);
        return userService.deleteUser(id);
    }

}

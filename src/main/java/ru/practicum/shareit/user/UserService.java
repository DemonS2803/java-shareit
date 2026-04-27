package ru.practicum.shareit.user;

import java.util.List;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUserById(Long id);

    UserDto saveUser(CreateUserDto user);

    UserDto updateUser(UpdateUserDto user);

    UserDto deleteUser(Long id);

    List<UserDto> getUsers();

}

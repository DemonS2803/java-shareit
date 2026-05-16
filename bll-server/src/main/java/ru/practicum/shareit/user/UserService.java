package ru.practicum.shareit.user;

import java.util.List;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUserById(Long id);

    // как лучше сделать контракт между сервисами?
    // 1. хочется выстроить связь между пакетами только через интерфейс,
    // чтобы в будущем безболезненно вынести логику куда захочется но тогда нет доступа до бд сущности
    // (в данном случае User), а она нужна, чтобы красиво оперировать данными в бд
    // 2. можно сделать repository доступными из других пакетов, но тогда опять же повышается связность
    // и красота разбиения по пакетам уже не так заметна
    // Пока что обойдусь самым простым решением...
    User getUserEntityById(Long id);

    UserDto saveUser(CreateUserDto user);

    UserDto updateUser(UpdateUserDto user);

    UserDto deleteUser(Long id);

    List<UserDto> getUsers();

}

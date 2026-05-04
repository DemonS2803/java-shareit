package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

interface UserRepository {

    User save(User user);

    User update(User user);

    User delete(Long id);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    List<User> findAllUsers();

}

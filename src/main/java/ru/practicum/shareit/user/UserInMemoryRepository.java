package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.RepositoryException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
class UserInMemoryRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long primaryKey = 1L;

    @Override
    public User save(User user) {
        if (user == null) {
            throw new RepositoryException("Cannot save null user");
        }
        user.setId(primaryKey++);
        log.info("Saving user {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user == null) {
            throw new RepositoryException("Cannot update null user");
        }
        if (!users.containsKey(user.getId())) {
            throw new RepositoryException("Cannot update non existing user");
        }
        log.info("Updating user {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Cannot delete non existing user");
        }
        User user = users.remove(id);
        log.info("Deleted user {}", user);
        return user;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}

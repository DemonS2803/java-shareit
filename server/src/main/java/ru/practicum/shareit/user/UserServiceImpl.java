package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

import ru.practicum.shareit.common.exception.DuplicateDataException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
        User user = getUserByIdOrThrow(id);
        log.debug("Get user by id: {}", user);
        return UserMapper.toDto(user);
    }

    @Override
    public User getUserEntityById(Long id) {
        return getUserByIdOrThrow(id);
    }

    private User getUserByIdOrThrow(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @Override
    public UserDto saveUser(CreateUserDto dto) {
        User user = UserMapper.fromDto(dto);
        validateUser(user);
        log.info("Save user: {}", user);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UpdateUserDto dto) {
        User dbUser = getUserByIdOrThrow(dto.getId());
        if (dto.getName() != null && !dto.getName().isBlank()) {
            dbUser.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            dbUser.setEmail(dto.getEmail());
            validateUserEmailExists(dbUser);
        }
        if (dto.getSurname() != null && !dto.getSurname().isBlank()) {
            dbUser.setSurname(dto.getSurname());
        }
        log.info("Update user: {}", dbUser);
        return UserMapper.toDto(userRepository.save(dbUser));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto deleteUser(Long id) {
        User user = getUserByIdOrThrow(id);
        log.info("Delete user: {}", user);
        userRepository.deleteById(id);
        return UserMapper.toDto(user);
    }

    private void validateUser(User user) {
        validateUserEmailExists(user);
    }

    private void validateUserEmailExists(User user) {
        Optional<User> dbUser = userRepository.findUserByEmail(user.getEmail());
        if (dbUser.isPresent() && !dbUser.get().getId().equals(user.getId())) {
            throw new DuplicateDataException("User with email " + user.getEmail() + " already exists");
        }
    }
}

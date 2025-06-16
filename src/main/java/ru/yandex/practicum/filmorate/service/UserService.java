package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    final UserStorage userStorage;

    public List<User> getUsers() {
        log.info("Запрос всех пользователей");
        return userStorage.getUsers();
    }

    public User getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user != null) {
            return user;
        }
        throw new NotFoundException("Пользователь не найден с id = " + userId);
    }

    public User createUser(User user) {
        checkEmail(user.getEmail());
        checkLogin(user.getLogin());
        checkBirthday(user.getBirthday());

        user.setId(getNextId());
        user.setNameWithCheck(user);

        userStorage.addOrUpdateUser(user);
        log.info("Пользователь добавлен");
        return user;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.debug("Не указан Id при обновлении пользователя");
            throw new ValidationException("Не указан Id при обновлении пользователя");
        }


        User oldUser = userStorage.getUserById(newUser.getId());
        if (oldUser == null) {
            log.debug("Пользователь с id = " + newUser.getId() + " не найден");
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        checkEmail(newUser.getEmail());
        checkLogin(newUser.getLogin());
        checkBirthday(newUser.getBirthday());

        oldUser.setEmail(newUser.getEmail());
        oldUser.setNameWithCheck(newUser);
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());
        userStorage.addOrUpdateUser(oldUser);
        log.info("Пользователь с id " + newUser.getId() + " обновлен");
        return oldUser;
    }

    public List<User> getFriendsByUserId(Long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriends(userId, friendId);
        userStorage.addFriends(friendId, userId);
    }

    public void removeFriends(Long userId, Long friendId) {
        userStorage.removeFriends(userId, friendId);
        userStorage.removeFriends(friendId, userId);
    }

    private long getNextId() {
        long currentMaxId = userStorage.getUsers()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkEmail(String email) {
        boolean existEmail = userStorage.getUsers().stream()
                .map(User::getEmail)
                .anyMatch(email::equals);
        if (existEmail) {
            log.debug("Пользователь с почтой " + email + " уже существует");
            throw new AlreadyExistsException("Пользователь с почтой " + email + " уже существует");
        }
    }

    private void checkLogin(String login) {
        if (login.contains(" ")) {
            log.debug("Логин должен быть без пробелов");
            throw new ValidationException("Логин должен быть без пробелов");
        }
        boolean existEmail = userStorage.getUsers().stream()
                .map(User::getLogin)
                .anyMatch(login::equals);
        if (existEmail) {
            log.debug("Пользователь с логином " + login + " уже существует");
            throw new AlreadyExistsException("Пользователь с логином " + login + " уже существует");
        }
    }

    private void checkBirthday(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            log.debug("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}

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
        log.info("Получение пользователя с id = " + userId);
        return getUserByIdWithCheck(userId);
    }

    public User createUser(User user) {
        checkEmail(user.getEmail());
        checkLogin(user.getLogin());
        checkBirthday(user.getBirthday());

        user.setId(getNextId());
        user.setNameWithCheck(user);

        userStorage.createUser(user);
        log.info("Пользователь добавлен");
        return user;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.debug("Не указан Id при обновлении пользователя");
            throw new ValidationException("Не указан Id при обновлении пользователя");
        }

        User oldUser = getUserByIdWithCheck(newUser.getId());

        checkEmail(newUser.getEmail());
        checkLogin(newUser.getLogin());
        checkBirthday(newUser.getBirthday());

        oldUser.setEmail(newUser.getEmail());
        oldUser.setNameWithCheck(newUser);
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());
        userStorage.updateUser(oldUser);
        log.info("Пользователь с id " + newUser.getId() + " обновлен");
        return oldUser;
    }

    public List<User> getFriendsByUserId(Long userId) {
        User user = getUserByIdWithCheck(userId);
        log.info("Получение друзей пользователя с id = " + userId);
        return userStorage.getFriends(user);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUserByIdWithCheck(userId);
        User otherUser = getUserByIdWithCheck(otherUserId);
        log.info("Получение общих друзей пользователей с id = " + userId + " и " + otherUserId);
        return userStorage.getCommonFriends(user, otherUser);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserByIdWithCheck(userId);
        getUserByIdWithCheck(friendId);
        userStorage.addFriends(user, friendId);
        log.info("Пользователи с id = " + userId + " и " + friendId + " теперь друзья");
    }

    public void removeFriends(Long userId, Long friendId) {
        User user = getUserByIdWithCheck(userId);
        getUserByIdWithCheck(friendId);
        userStorage.removeFriends(user, friendId);
        log.info("Пользователи с id = " + userId + " и " + friendId + " перестали быть друзьями");
    }

    private User getUserByIdWithCheck(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
           log.debug("Пользователь не найден с id = " + id);
            throw new NotFoundException("Пользователь не найден с id = " + id);
        }
        return user;
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

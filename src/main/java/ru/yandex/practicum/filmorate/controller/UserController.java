package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        checkEmail(user.getEmail());
        checkLogin(user.getLogin());
        checkBirthday(user.getBirthday());

        user.setId(getNextId());
        user.setNameWithCheck(user);

        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.debug("Не указан Id при обновлении пользователя");
            throw new ValidationException("Не указан Id при обновлении пользователя");
        }
        checkEmail(newUser.getEmail());
        checkLogin(newUser.getLogin());
        checkBirthday(newUser.getBirthday());

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setNameWithCheck(newUser);
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь с id " + newUser.getId() + " обновлен");
            return oldUser;
        }
        log.debug("Пользователь с id = " + newUser.getId() + " не найден");
        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkEmail(String email) {
        boolean existEmail = users.values().stream()
                .map(User::getEmail)
                .anyMatch(email::equals);
        if (existEmail) {
            log.debug("Пользователь с почтой " + email + " уже существует");
            throw new ValidationException("Пользователь с почтой " + email + " уже существует");
        }
    }

    private void checkLogin(String login) {
        if (login.contains(" ")) {
            log.debug("Логин должен быть без пробелов");
            throw new ValidationException("Логин должен быть без пробелов");
        }
        boolean existEmail = users.values().stream()
                .map(User::getLogin)
                .anyMatch(login::equals);
        if (existEmail) {
            log.debug("Пользователь с логином " + login + " уже существует");
            throw new ValidationException("Пользователь с логином " + login + " уже существует");
        }
    }

    private void checkBirthday(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            log.debug("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}

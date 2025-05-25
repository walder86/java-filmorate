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
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        if (checkEmail(user.getEmail())) {
            log.debug("Пользователь с почтой " + user.getEmail() + " уже существует");
            throw new ValidationException("Пользователь с почтой " + user.getEmail() + " уже существует");
        }
        if (checkLogin(user.getLogin())) {
            log.debug("Пользователь с логином " + user.getLogin() + " уже существует");
            throw new ValidationException("Пользователь с логином " + user.getLogin() + " уже существует");
        }
        if (user.getLogin().contains(" ")) {
            log.debug("Логин должен быть без пробелов");
            throw new ValidationException("Логин должен быть без пробелов");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
        else user.setName(user.getName());

        users.put(user.getId(), user);
        log.info("Фильм добавлен");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            log.debug("Не указан Id при обновлении пользователя");
            throw new ValidationException("Не указан Id при обновлении пользователя");
        }
        if (checkEmail(newUser.getEmail())) {
            log.debug("Пользователь с почтой " + newUser.getEmail() + " уже существует");
            throw new ValidationException("Пользователь с почтой " + newUser.getEmail() + " уже существует");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setName(newUser.getName());
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

    private boolean checkEmail(String email) {
        return users.values().stream()
                .map(User::getEmail)
                .anyMatch(email::equals);
    }

    private boolean checkLogin(String login) {
        return users.values().stream()
                .map(User::getEmail)
                .anyMatch(login::equals);
    }
}

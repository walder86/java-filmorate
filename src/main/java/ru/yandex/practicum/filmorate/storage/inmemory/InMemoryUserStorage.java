package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public void addOrUpdateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        if (user == null) {
            log.error("Пользователь с id = " + userId + " не найден");
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        user.addFriend(friendId);
    }

    @Override
    public void removeFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        if (user == null) {
            log.error("Пользователь с id = " + userId + " не найден");
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        user.removeFriend(friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.error("Пользователь с id = " + id + " не найден");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        List<Long> friendIds = user.getFriends();
        List<User> friends = friendIds.stream()
                .map(users::get)
                .toList();
        return new ArrayList<>(friends);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherUserId) {
        User user = users.get(id);
        if (user == null) {
            log.error("Пользователь с id = " + id + " не найден");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        User otherUser = users.get(otherUserId);
        if (otherUser == null) {
            log.error("Пользователь с id = " + id + " не найден");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        List<User> commonFriends = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(users::get)
                .toList();
        return new ArrayList<>(commonFriends);
    }
}

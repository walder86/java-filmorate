package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
    public void addFriends(User user, Long friendId) {
        user.addFriend(friendId);
    }

    @Override
    public void removeFriends(User user, Long friendId) {
        user.removeFriend(friendId);
    }

    @Override
    public List<User> getFriends(User user) {
        List<Long> friendIds = user.getFriends();
        List<User> friends = friendIds.stream()
                .map(users::get)
                .toList();
        return new ArrayList<>(friends);
    }

    @Override
    public List<User> getCommonFriends(User user, User otherUser) {
        List<User> commonFriends = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(users::get)
                .toList();
        return new ArrayList<>(commonFriends);
    }
}

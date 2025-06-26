package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUserById(Long id);

    void createUser(User user);

    void updateUser(User user);

    void addFriends(User user, Long friendId);

    void removeFriends(User user, Long friendId);

    List<User> getFriends(User user);

    List<User> getCommonFriends(User user, User otherUser);
}

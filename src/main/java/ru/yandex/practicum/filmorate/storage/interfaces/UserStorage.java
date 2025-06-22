package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public List<User> getUsers();

    public User getUserById(Long id);

    public void createUser(User user);

    public void updateUser(User user);

    void addFriends(User user, Long friendId);

    void removeFriends(User user, Long friendId);

    List<User> getFriends(User user);

    List<User> getCommonFriends(User user, User otherUser);
}

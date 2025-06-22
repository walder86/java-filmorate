package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Primary
@Component
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper;

    @Override
    public List<User> getUsers() {
        List<User> users = jdbcTemplate.query("SELECT " +
                "u.ID, " +
                "u.EMAIL, " +
                "u.LOGIN, " +
                "u.NAME, " +
                "u.BIRTHDAY, " +
                "f.FRIEND_ID " +
                "FROM USERS u " +
                "LEFT JOIN FRIENDS f ON (f.USER_ID  = u.ID)", mapper);
        Set<User> uniqueUser = new TreeSet<>(Comparator.comparing(User::getId));
        uniqueUser.addAll(users);
        return new ArrayList<>(uniqueUser);
    }

    @Override
    public User getUserById(Long id) {
        List<User> users = jdbcTemplate.query("SELECT " +
                "u.ID, " +
                "u.EMAIL, " +
                "u.LOGIN, " +
                "u.NAME, " +
                "u.BIRTHDAY, " +
                "f.FRIEND_ID " +
                "FROM USERS AS u " +
                "LEFT JOIN FRIENDS AS f ON (f.USER_ID  = u.ID)" +
                "WHERE u.id = ?", mapper, id);
        if (users.size() == 0) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public void createUser(User user) {
        String sqlQuery =
                "INSERT INTO users (email, login, name, birthday)values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
    }

    @Override
    public void updateUser(User user) {
        String sqlQuery =
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
    }

    @Override
    public void addFriends(User user, Long friendId) {
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, status)values (?, ?, ?)", user.getId(), friendId, true);
    }

    @Override
    public void removeFriends(User user, Long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", user.getId(), friendId);
    }

    @Override
    public List<User> getFriends(User user) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id IN (SELECT friend_id FROM friends WHERE user_id = ? AND status = true)", new DataClassRowMapper<>(User.class), user.getId());
    }

    @Override
    public List<User> getCommonFriends(User user, User otherUser) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id IN (SELECT friend_id FROM friends WHERE user_id = ? AND status = true AND friend_id IN ( SELECT friend_id FROM friends WHERE user_id = ? AND status = true))", new DataClassRowMapper<>(User.class), user.getId(), otherUser.getId());
    }
}

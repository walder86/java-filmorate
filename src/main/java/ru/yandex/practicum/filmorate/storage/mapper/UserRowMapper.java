package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRowMapper implements RowMapper<User> {

    Map<Long, User> userMap = new HashMap<>();

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        Long userId = resultSet.getLong("id");
        User user = userMap.get(userId);

        if (user == null) {

            user = User.builder()
                    .id(userId)
                    .email(resultSet.getString("email"))
                    .login(resultSet.getString("login"))
                    .name(resultSet.getString("name"))
                    .birthday(resultSet.getDate("birthday").toLocalDate())
                    .friends(new ArrayList<>())
                    .build();
            userMap.put(userId, user);
        }

        // если есть friends:
        if (resultSet.getLong("FRIEND_ID") != 0) {
            if (!user.getFriends().contains(resultSet.getLong("FRIEND_ID"))) {
                user.getFriends().add(resultSet.getLong("FRIEND_ID"));
            }
        }
        if (resultSet.isLast()) {
            userMap.clear();
        }

        return user;
    }
}

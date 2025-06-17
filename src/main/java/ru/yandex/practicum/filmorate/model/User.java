package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @Email
    @NotNull
    private String email;

    @NotBlank
    private String login;

    private String name;

    @NotNull
    private LocalDate birthday;

    private List<Long> friends = new ArrayList<>();

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    public void setNameWithCheck(User user) {
        if (user.getName() == null || user.getName().isBlank())
            this.setName(user.getLogin());
        else this.setName(user.getName());
    }
}

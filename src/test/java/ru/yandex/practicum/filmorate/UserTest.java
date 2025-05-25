package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

public class UserTest {

    private final User user = User
            .builder()
            .login("a_b")
            .name("Nick Name")
            .email("mail@mail.ru")
            .birthday(LocalDate.now())
            .build();
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void shouldCreateUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertTrue(violations.isEmpty());
    }

}

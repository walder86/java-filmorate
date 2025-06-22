package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingMPAStorage;

import java.util.List;

@Component
@AllArgsConstructor
public class RatingMPADbStorage implements RatingMPAStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<RatingMPA> getAll() {
        return jdbcTemplate.query("SELECT * FROM rating_mpa", new DataClassRowMapper<>(RatingMPA.class));
    }

    @Override
    public RatingMPA getById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM rating_mpa WHERE id = ?", new DataClassRowMapper<>(RatingMPA.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public RatingMPA getRatingMPAOfFilm(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM rating_mpa WHERE id IN (SELECT rating_mpa_id FROM films WHERE id = ?);", new DataClassRowMapper<>(RatingMPA.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

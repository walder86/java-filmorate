package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

public interface RatingMPAStorage {

    List<RatingMPA> getAll();

    RatingMPA getById(Long id);

    RatingMPA getRatingMPAOfFilm(Long id);
}

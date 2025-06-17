package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film getFilmById(Long id);

    void addOrUpdateFilm(Film film);

    void addLike(Film film, Long userId);

    void removeLike(Film film, Long userId);

    List<Film> getPopularFilms(Long count);
}

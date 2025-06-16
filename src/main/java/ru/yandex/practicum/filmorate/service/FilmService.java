package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    final UserService userService;

    final FilmStorage filmStorage;

    public List<Film> getFilms() {
        log.info("Запрос всех фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            return film;
        }
        throw new NotFoundException("Фильм не найден с id = " + filmId);
    }

    public Film create(Film film) {
        checkDate(film);

        film.setId(getNextId());

        filmStorage.addOrUpdateFilm(film);
        log.info("Фильм добавлен");
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.debug("Не указан Id при обновлении фильма");
            throw new ValidationException("Не указан Id при обновлении фильма");
        }

        Film oldFilm = filmStorage.getFilmById(newFilm.getId());
        if (oldFilm == null) {
            log.debug("Фильм с id = " + newFilm.getId() + " не найден");
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        checkDate(newFilm);

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        filmStorage.addOrUpdateFilm(oldFilm);
        log.info("Фильм с id " + newFilm.getId() + " обновлен");
        return oldFilm;
    }

    public void addLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        filmStorage.removeLike(filmId, userId);
    }

    private long getNextId() {
        long currentMaxId = filmStorage.getFilms()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public List<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
    }
}

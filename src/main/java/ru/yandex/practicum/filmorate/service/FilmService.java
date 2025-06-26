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

    private final GenreService genreService;

    private final RatingMPAService ratingMPAService;

    public List<Film> getFilms() {
        log.info("Запрос всех фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long filmId) {
        log.info("Получение фильма с id = " + filmId);
        return getFilmByIdWithCheck(filmId);
    }

    public Film create(Film film) {
        checkDate(film);

        film.setId(getNextId());

        if (film.getMpa() != null) {
            ratingMPAService.getById(film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                genreService.getById(genre.getId());
            });
        }
        filmStorage.createFilm(film);
        log.info("Фильм добавлен");
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.debug("Не указан Id при обновлении фильма");
            throw new ValidationException("Не указан Id при обновлении фильма");
        }

        Film oldFilm = getFilmByIdWithCheck(newFilm.getId());

        checkDate(newFilm);

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        filmStorage.updateFilm(oldFilm);
        log.info("Фильм с id " + newFilm.getId() + " обновлен");
        return oldFilm;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmByIdWithCheck(filmId);
        userService.getUserById(userId);
        filmStorage.addLike(film, userId);
        log.info("Лайк добавлен к фильму с id = " + filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmByIdWithCheck(filmId);
        userService.getUserById(userId);
        filmStorage.removeLike(film, userId);
        log.info("Лайк удален с фильма с id = " + filmId);
    }

    private Film getFilmByIdWithCheck(Long id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.debug("Фильм не найден с id = " + id);
            throw new NotFoundException("Фильм не найден с id = " + id);
        }
        return film;
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
        if (count <= 0) {
            throw new ValidationException("count должен быть больше 0");
        }
        log.info("Запрос " + count + " популярных фильмов");
        return filmStorage.getPopularFilms(count);
    }
}

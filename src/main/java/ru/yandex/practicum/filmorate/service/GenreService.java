package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage storage;

    public List<Genre> getAll() {
        log.info("Запрос всех жанров");
        return storage.getAll();
    }

    public Genre getById(Long id) {
        log.info("Запрос жанра с id = " + id);
        return storage.getById(id);
    }
}

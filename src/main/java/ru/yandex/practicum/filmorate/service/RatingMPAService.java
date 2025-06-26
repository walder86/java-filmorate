package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingMPAStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingMPAService {

    private final RatingMPAStorage storage;

    public List<RatingMPA> getAll() {
        log.info("Запрос всех рейтингов MPA");
        return storage.getAll();
    }

    public RatingMPA getById(Long id) {
        log.info("Запрос рейтинга MPA с id = " + id);
        return storage.getById(id);
    }
}

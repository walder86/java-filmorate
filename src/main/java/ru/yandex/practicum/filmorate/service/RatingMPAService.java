package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingMPAStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingMPAService {

    private final RatingMPAStorage storage;

    public List<RatingMPA> getAll() {
        return storage.getAll();
    }

    public RatingMPA getById(Long id) {
        if (storage.getById(id) == null) {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден");
        } else {
            return storage.getById(id);
        }

    }
}

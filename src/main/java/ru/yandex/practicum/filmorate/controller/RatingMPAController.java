package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.RatingMPAService;

import java.util.List;

@RestController
@RequestMapping(value = "/mpa")
@RequiredArgsConstructor
public class RatingMPAController {

    private final RatingMPAService service;

    @GetMapping
    public List<RatingMPA> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RatingMPA getById(@PathVariable Long id) {
        return service.getById(id);
    }
}

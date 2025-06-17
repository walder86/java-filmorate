package ru.yandex.practicum.filmorate.exception;

import lombok.Data;

@Data
class ErrorResponse {
    private final String error;
    private final String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

}

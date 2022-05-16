package ru.yandex.practicum.filmorate.validator;

import org.springframework.validation.Errors;

public class FilmorateValidationErrorBuilder {
    public static FilmorateValidationError fromBindingErrors(Errors errors) {
        FilmorateValidationError error = new FilmorateValidationError(
                "Validation failure: " + errors.getErrorCount() + " errors.");
        errors.getAllErrors().forEach(e -> error.addValidationError(e.getDefaultMessage()));
        return error;
    }
}

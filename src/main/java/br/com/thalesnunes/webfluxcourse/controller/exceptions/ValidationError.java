package br.com.thalesnunes.webfluxcourse.controller.exceptions;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ValidationError extends StandardError implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<FieldError> errors = new ArrayList<>();

    public ValidationError(LocalDateTime timestamp, String path, Integer status, String error, String message) {
        super(timestamp, path, status, error, message);
    }

    public void addError(String fieldName, String message){
        this.errors.add(new FieldError(fieldName, message));
    }

    @Getter
    @AllArgsConstructor
    private static final class FieldError {
        private String fieldName;
        private String message;

    }
}

package br.com.thalesnunes.webfluxcourse.model.request;

import br.com.thalesnunes.webfluxcourse.validator.TrimString;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @TrimString
        @NotBlank(message = "must not be null or empty")
        @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
        String name,
        @TrimString
        @NotBlank(message = "must not be null or empty")
        @Email(message = "invalid email")
        String email,
        @TrimString
        @NotBlank(message = "must not be null or empty")
        @Size(min = 3, max = 20, message = "must be between 3 and 20 characters")
        String password
) {
}

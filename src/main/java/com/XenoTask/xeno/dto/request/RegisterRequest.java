package com.xenotask.xeno.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(max = 255)
    private String firstName;

    @NotBlank
    @Size(max = 255)
    private String lastName;

    @Email
    @NotBlank
    @Size(max = 320)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}


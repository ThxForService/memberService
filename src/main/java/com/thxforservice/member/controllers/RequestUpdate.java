package com.thxforservice.member.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestUpdate {
    @NotBlank
    private String userName;

    @Size(min = 8)
    private String password;

    private String confirmPassword;

    private String mobile;
}

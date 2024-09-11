package com.thxforservice.mypage.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RequestProfile {
    @NotBlank
    private String userName;

    private String password;

    private String confirmPassword;

    private String mobile;
}
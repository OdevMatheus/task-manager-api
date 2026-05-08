package com.matheushenrique.todosimple.models.DTOs;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
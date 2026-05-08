package com.matheushenrique.todosimple.models.DTOs;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    @NotBlank
    private String description;
}

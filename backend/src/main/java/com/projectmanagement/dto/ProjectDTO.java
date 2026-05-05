package com.projectmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Le titre du projet est requis")
    private String title;

    private String description;

    @NotNull(message = "La date de début est requise")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est requise")
    private LocalDate endDate;
}

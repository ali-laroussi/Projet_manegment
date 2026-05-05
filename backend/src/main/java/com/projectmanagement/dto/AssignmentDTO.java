package com.projectmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {
    private Long id;

    @NotNull(message = "L'ID de l'employé est requis")
    private Long employeeId;

    private String employeeName;

    @NotNull(message = "L'ID du projet est requis")
    private Long projectId;

    private String projectTitle;

    @NotNull(message = "La date de début est requise")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est requise")
    private LocalDate endDate;
}

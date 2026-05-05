package com.projectmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateDTO {
    @NotNull(message = "L'employe destinataire est requis")
    private Long employeeId;

    @NotBlank(message = "Le message est requis")
    @Size(max = 1000, message = "Le message ne peut pas depasser 1000 caracteres")
    private String message;
}

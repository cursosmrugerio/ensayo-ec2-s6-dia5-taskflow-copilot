package com.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de entrada para PATCH /tasks/{id}/assignee — solo contiene el id del nuevo responsable.
 */
public record TaskAssigneeUpdateRequest(
        @NotNull(message = "El id del responsable es obligatorio.")
        @Positive(message = "El id del responsable debe ser un número positivo.")
        Long assigneeId
) {
}

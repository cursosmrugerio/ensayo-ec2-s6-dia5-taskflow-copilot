package com.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de entrada para PATCH /tasks/{id}/assignee — solo contiene el id del nuevo responsable.
 */
public record TaskAssigneeUpdateRequest(
        @NotNull
        @Positive
        Long assigneeId
) {
}

package com.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * TaskAssigneeUpdateRequest — el cuerpo del PATCH /tasks/{id}/assignee (MP-10). Un DTO por OPERACIÓN:
 * el único campo que este endpoint puede tocar es el responsable (assigneeId).
 *
 * @NotNull y @Positive: si el JSON trae {"assigneeId": null}, falta la clave o trae un número
 * menor que 1, la validación da 400.
 */
public record TaskAssigneeUpdateRequest(

        @NotNull(message = "El responsable es obligatorio.")
        @Positive(message = "El id del responsable debe ser positivo.")
        Long assigneeId
) {
}

package com.taskflow.slice;

import com.taskflow.controller.TaskController;
import com.taskflow.dto.TaskResponse;
import com.taskflow.exception.TaskStateException;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.security.JwtAuthenticationFilter;
import com.taskflow.service.ProjectService;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Slice web de PATCH /tasks/{id}/assignee: capa HTTP; lógica probada en unit. */
@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReasignarTareaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void patchAssignee_tareaExistente_devuelve200() throws Exception {
        Task tarea = new Task(1L, "Tarea", "Desc", TaskStatus.TODO, Priority.MED, 1L, null, null);
        Task reasignada = new Task(1L, "Tarea", "Desc", TaskStatus.TODO, Priority.MED, 1L, 2L, null);

        when(taskService.buscarPorId(1L)).thenReturn(Optional.of(tarea));
        when(taskService.reasignar(any(Task.class), eq(2L))).thenReturn(reasignada);

        mockMvc.perform(patch("/tasks/1/assignee")
                        .contentType("application/json")
                        .content("{\"assigneeId\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeId").value(2));
    }

    @Test
    void patchAssignee_tareaEnDONE_devuelve422() throws Exception {
        Task tarea = new Task(1L, "Tarea", "Desc", TaskStatus.DONE, Priority.MED, 1L, 1L, null);

        when(taskService.buscarPorId(1L)).thenReturn(Optional.of(tarea));
        when(taskService.reasignar(any(Task.class), eq(2L)))
                .thenThrow(new TaskStateException("No se puede reasignar una tarea terminada."));

        mockMvc.perform(patch("/tasks/1/assignee")
                        .contentType("application/json")
                        .content("{\"assigneeId\": 2}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("No se puede reasignar una tarea terminada."));
    }

    @Test
    void patchAssignee_tareaInexistente_devuelve404() throws Exception {
        when(taskService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/tasks/99/assignee")
                        .contentType("application/json")
                        .content("{\"assigneeId\": 2}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchAssignee_cuerpoVacio_devuelve400() throws Exception {
        mockMvc.perform(patch("/tasks/1/assignee")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
        verify(taskService, never()).reasignar(any(), any());
    }
}

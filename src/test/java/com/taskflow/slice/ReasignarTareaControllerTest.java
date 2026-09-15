package com.taskflow.slice;

import com.taskflow.controller.TaskController;
import com.taskflow.exception.TaskStateException;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.dto.TaskAssigneeUpdateRequest;
import com.taskflow.security.JwtAuthenticationFilter;
import com.taskflow.service.ProjectService;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void patchAssignee_existente_devuelve200ConAssignee() throws Exception {
        org.mockito.Mockito.doReturn(Optional.of(new Task(4L, "T04", "d", TaskStatus.TODO, null, 1L, null, null))).when(taskService).buscarPorId(4L);
        org.mockito.Mockito.doReturn(new Task(4L, "T04", "d", TaskStatus.TODO, null, 1L, 2L, null)).when(taskService).reasignar(any(Task.class), org.mockito.ArgumentMatchers.eq(2L));

        mockMvc.perform(patch("/tasks/4/assignee").contentType("application/json").content("{\"assigneeId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeId").value(2));
    }

    @Test
    void patchAssignee_tareaDone_devuelve422() throws Exception {
        org.mockito.Mockito.doReturn(Optional.of(new Task(2L, "T02", "d", TaskStatus.DONE, null, 1L, 3L, null))).when(taskService).buscarPorId(2L);
        org.mockito.Mockito.doThrow(new TaskStateException("No se puede reasignar una tarea terminada.")).when(taskService).reasignar(any(Task.class), any(Long.class));

        mockMvc.perform(patch("/tasks/2/assignee").contentType("application/json").content("{\"assigneeId\":3}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("No se puede reasignar una tarea terminada."));
    }

    @Test
    void patchAssignee_inexistente_devuelve404() throws Exception {
        org.mockito.Mockito.doReturn(Optional.empty()).when(taskService).buscarPorId(99L);

        mockMvc.perform(patch("/tasks/99/assignee").contentType("application/json").content("{\"assigneeId\":2}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void patchAssignee_cuerpoVacio_devuelve400_y_reasignarNuncaEsLlamado() throws Exception {
        org.mockito.Mockito.doReturn(Optional.of(new Task(6L, "T06", "d", TaskStatus.TODO, null, 1L, null, null))).when(taskService).buscarPorId(6L);

        mockMvc.perform(patch("/tasks/6/assignee").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).reasignar(any(Task.class), any(Long.class));
    }
}

package com.taskflow.unit;

import com.taskflow.exception.TaskStateException;
import com.taskflow.exception.TaskValidationException;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.TaskRepository;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Unit de TaskService.reasignar: sin Spring, repositorio mockeado, tareas reales. */
@ExtendWith(MockitoExtension.class)
class ReasignarTareaServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService service;

    @Test
    void reasignar_tareaEnTODO_asignaYGuarda() throws TaskValidationException {
        Task tarea = new Task(1L, "Tarea", "Desc", TaskStatus.TODO, Priority.MED, 1L, null, null);
        when(taskRepository.save(any(Task.class))).thenReturn(tarea);

        service.reasignar(tarea, 2L);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task guardada = captor.getValue();
        assertEquals(2L, guardada.getAssigneeId());
    }

    @Test
    void reasignar_tareaEnDONE_lanzaTaskStateException() throws TaskValidationException {
        Task tarea = new Task(1L, "Tarea", "Desc", TaskStatus.DONE, Priority.MED, 1L, 1L, null);

        assertThrows(TaskStateException.class, () -> service.reasignar(tarea, 2L));
        verify(taskRepository, never()).save(any());
    }
}

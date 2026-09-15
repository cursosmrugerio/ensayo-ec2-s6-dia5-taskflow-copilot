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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReasignarTareaServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService service;

    private Task tareaTODO() throws TaskValidationException {
        return new Task(1L, "Tarea", "d", TaskStatus.TODO, Priority.MED, 1L, null, null);
    }

    private Task tareaDONE() throws TaskValidationException {
        return new Task(2L, "Tarea2", "d", TaskStatus.DONE, Priority.HIGH, 1L, 3L, null);
    }

    @Test
    void reasignar_tareaTODO_llamaSaveConAssignee() throws Exception {
        Task t = tareaTODO();
        // Llamamos al service por el camino directo: reasignar recibe la entidad y guarda.
        service.reasignar(t, 5L);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();
        assertEquals(5L, saved.getAssigneeId());
    }

    @Test
    void reasignar_tareaDONE_lanzaTaskStateException_y_noLlamaSave() throws Exception {
        Task t = tareaDONE();
        // No stubbing necesario: reasignar recibe la entidad y lanza según su estado.
        assertThrows(TaskStateException.class, () -> service.reasignar(t, 1L));
        verify(taskRepository, never()).save(t);
    }
}

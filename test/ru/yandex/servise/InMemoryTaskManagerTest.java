package ru.yandex.servise;

import org.junit.jupiter.api.BeforeAll;
import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;
import ru.yandex.servise.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;
    private Epic epic;

    @BeforeEach
    void setup() {
        manager = new InMemoryTaskManager();
        epic = manager.addEpic(new Epic("Epic summary", "Epic description"));
    }

    @Test
    void shouldSetStatusNewWhenAllSubtasksAreNew() {
        manager.addSubtask(new Subtask("Sub 1", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Sub 2", "Desc", Status.NEW, epic.getId()));

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus());
    }

    @Test
    void shouldSetStatusDoneWhenAllSubtasksAreDone() {
        manager.addSubtask(new Subtask("Sub 1", "Desc", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Sub 2", "Desc", Status.DONE, epic.getId()));

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus());
    }

    @Test
    void shouldSetStatusInProgressWhenMixedStatuses() {
        manager.addSubtask(new Subtask("Sub 1", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Sub 2", "Desc", Status.DONE, epic.getId()));

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldSetStatusInProgressWhenAllSubtasksInProgress() {
        manager.addSubtask(new Subtask("Sub 1", "Desc", Status.IN_PROGRESS, epic.getId()));
        manager.addSubtask(new Subtask("Sub 2", "Desc", Status.IN_PROGRESS, epic.getId()));

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldSetStatusNewWhenNoSubtasks() {
        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus());
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtaskIsDeleted() {
        // Добавим DONE и IN_PROGRESS сабтаски → эпик должен быть IN_PROGRESS
        Subtask sub1 = manager.addSubtask(new Subtask("Sub 1", "Desc", Status.DONE, epic.getId()));
        Subtask sub2 = manager.addSubtask(new Subtask("Sub 2", "Desc", Status.IN_PROGRESS, epic.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());

        // Удалим IN_PROGRESS сабтаску → осталась только DONE → статус эпика → DONE
        manager.removeSubtaskById(sub2.getId());
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());

        // Удалим последнюю сабтаску → статус эпика должен стать NEW
        manager.removeSubtaskById(sub1.getId());
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }
}
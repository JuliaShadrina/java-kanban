package ru.yandex.model;

import org.junit.jupiter.api.BeforeAll;
import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;
import ru.yandex.servise.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static TaskManager inMemoryTaskManager;

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    void addNewEpicTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);

        final Epic savedEpic = inMemoryTaskManager.addEpic(epic);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = inMemoryTaskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void epicCannotBeAddedToItselfAsSubtaskTest() {
        Epic epic1 = new Epic(1, "Эпик1", "ОписаниеЭпика1");
        Subtask subtask1 = new Subtask(1, "Подзадача1", "ОписаниеПодзадачи1", Status.NEW, 1);
        // epicId == own ID

        inMemoryTaskManager.addEpic(epic1);
        Subtask result = inMemoryTaskManager.addSubtask(subtask1);

        assertNull(result, "Подзадача не должна добавляться с epicId == own ID");
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Список подзадач должен остаться пустым");
    }
}
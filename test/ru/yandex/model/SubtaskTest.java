package ru.yandex.model;

import org.junit.jupiter.api.BeforeAll;
import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;
import ru.yandex.servise.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private static TaskManager inMemoryTaskManager;

    @BeforeAll
    static void beforeAll() {
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    void addNewSubtask() {
        Subtask subtask = new Subtask("Подзадача1", "ОписаниеПодзадачи1", Status.NEW, 1);

        final Subtask savedSubtask = inMemoryTaskManager.addSubtask(subtask);
        assertNotNull(savedSubtask, "Сабтаска не найдена.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = inMemoryTaskManager.getSubtasks();
        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтаск.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
    }


    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Test Epic", "Test Epic description");
        inMemoryTaskManager.addEpic(epic);
        // Создаем подзадачу, где epicId равен id самой подзадачи
        Subtask subtask = new Subtask("Invalid Subtask", "Invalid description",
                Status.NEW, 1); // Устанавливаем epicId = 1
        subtask.setId(1); // Устанавливаем id подзадачи тоже 1

        // Пытаемся добавить подзадачу
        Subtask result = inMemoryTaskManager.addSubtask(subtask);

        assertNull(result, "Подзадача не должна добавляться, когда она является своим же эпиком");
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(),
                "Список подзадач должен остаться пустым после попытки добавить невалидную подзадачу");
    }
}
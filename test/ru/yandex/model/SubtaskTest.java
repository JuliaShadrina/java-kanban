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

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    void addNewSubtaskTest() {
        Epic epic = new Epic("Test Epic", "Test Epic description");
        inMemoryTaskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "ОписаниеПодзадачи1", Status.NEW, epic.getId());

        final Subtask savedSubtask = inMemoryTaskManager.addSubtask(subtask);
        assertNotNull(savedSubtask, "Сабтаска не найдена.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = inMemoryTaskManager.getSubtasks();
        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтаск.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
    }


    @Test
    void subtaskCannotBeItsOwnEpicTest() {
        Epic epic = new Epic("Test Epic", "Test Epic description");
        inMemoryTaskManager.addEpic(epic);
        // Создаем подзадачу, где epicId равен id самой подзадачи и равен Id созданного эпика
        Subtask subtask = new Subtask("Invalid Subtask", "Invalid description",
                Status.NEW, epic.getId()); // Устанавливаем epicId = Id добавленного эпика
        subtask.setId(epic.getId()); // Устанавливаем id подзадачи тоже = Id добавленного эпика

        // Пытаемся добавить подзадачу
        Subtask result = inMemoryTaskManager.addSubtask(subtask);

        assertNull(result, "Подзадача не должна добавляться, когда она является своим же эпиком");
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(),
                "Список подзадач должен остаться пустым после попытки добавить невалидную подзадачу");
    }
}
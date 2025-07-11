package ru.yandex.model;

import ru.yandex.model.conctants.Status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntentTest {

    @Test
    void testEqualsEpics() {
        Epic epic1 = new Epic(1, "Эпик1", "ОписаниеЭпика1", Status.DONE);
        Epic epic2 = new Epic(1, "Эпик2", "ОписаниеЭпика2", Status.NEW);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }
    @Test
    void testEqualsTasks() {
        Task task1 = new Task(1,"Задача1", "ОписаниеЗадачи1", Status.DONE);
        Task task2 = new Task(1, "Задача 2", "ОписаниеЗадачи2", Status.NEW);

        assertEquals(task2, task1, "Задачи с одинаковым id должны быть равны");
    }
    @Test
    void testEqualsEpicSubtasks() {
        Subtask subtask1 = new Subtask(1,"Подзадача1", "ОписаниеПодзадачи1",
                Status.DONE, 1);
        Subtask subtask2 = new Subtask(1,"Подзадача2", "ОписаниеПодзадачи2",
                Status.IN_PROGRESS, 2);

        assertEquals(subtask2, subtask1, "Задачи с одинаковым id должны быть равны");
    }
}
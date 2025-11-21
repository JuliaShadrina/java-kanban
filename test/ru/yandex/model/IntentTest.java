package ru.yandex.model;

import ru.yandex.model.conctants.Status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class IntentTest {

    @Test
    void equalsEpicsTest() {
        Epic epic1 = new Epic(1, "Эпик1", "ОписаниеЭпика1", Status.DONE);
        epic1.setDuration(120);
        epic1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));

        Epic epic2 = new Epic(1, "Эпик2", "ОписаниеЭпика2", Status.NEW);
        epic2.setDuration(180);
        epic2.setStartTime(LocalDateTime.of(2024, 1, 2, 10, 0));

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void equalsTasksTest() {
        Task task1 = new Task(1,"Задача1", "ОписаниеЗадачи1", Status.DONE);
        task1.setDuration(60);
        task1.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));

        Task task2 = new Task(1, "Задача 2", "ОписаниеЗадачи2", Status.NEW);
        task2.setDuration(90);
        task2.setStartTime(LocalDateTime.of(2024, 1, 1, 14, 0));

        assertEquals(task2, task1, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void equalsEpicSubtasksTest() {
        Subtask subtask1 = new Subtask(1,"Подзадача1", "ОписаниеПодзадачи1",
                Status.DONE, 1);
        subtask1.setDuration(30);
        subtask1.setStartTime(LocalDateTime.of(2024, 1, 1, 11, 0));

        Subtask subtask2 = new Subtask(1,"Подзадача2", "ОписаниеПодзадачи2",
                Status.IN_PROGRESS, 2);
        subtask2.setDuration(45);
        subtask2.setStartTime(LocalDateTime.of(2024, 1, 1, 15, 0));

        assertEquals(subtask2, subtask1, "Задачи с одинаковым id должны быть равны");
    }

}
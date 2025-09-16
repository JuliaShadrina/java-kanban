package ru.yandex.servise;

import org.junit.jupiter.api.Test;
import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;
import ru.yandex.servise.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addTest() {
        HistoryManager history = Managers.getDefaultHistory();
        Task task = new Task(1, "test", "desc", Status.NEW);

        history.add(task);
        final List<Intent> historyResult = history.getHistory();

        assertNotNull(historyResult, "История не должна быть null");
        assertEquals(1, historyResult.size(), "Неверное количество элементов в истории");
        assertEquals(task, historyResult.get(0), "Добавленная задача должна быть в истории");
    }

    @Test
    void shouldLimitHistorySizeTest() {
        HistoryManager history = Managers.getDefaultHistory();

        // Добавляем 11 задач
        for (int i = 1; i <= 11; i++) {
            history.add(new Task(i, "task" + i, "desc", Status.NEW));
        }

        assertEquals(10, history.getHistory().size(),
                "История должна содержать не более 10 элементов");
        assertEquals(11, history.getHistory().get(9).getId(),
                "В истории должна быть последняя добавленная задача");
    }

    @Test
    void shouldStoreDifferentTaskTypesTest() {
        HistoryManager history = Managers.getDefaultHistory();
        Task task = new Task(1, "task", "desc", Status.NEW);
        Epic epic = new Epic(2, "epic", "desc");

        history.add(task);
        history.add(epic);

        assertEquals(2, history.getHistory().size(),
                "История должна хранить все типы задач");
    }
}
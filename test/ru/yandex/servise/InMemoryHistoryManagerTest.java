package ru.yandex.servise;

import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;

import ru.yandex.model.Intent;
import ru.yandex.servise.util.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Тесты для проверки истории просмотров в InMemoryHistoryManager")
class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        // создаём 3 задачи разных типов
        task = new Task(1, "test", "desc", Status.NEW);
        epic = new Epic(2, "epic", "desc");
        subtask = new Subtask("subtask", "desc", Status.NEW, 2);

        // добавляем их в историю: task → epic → subtask
        historyManager.addToHistory(task);
        historyManager.addToHistory(epic);
        historyManager.addToHistory(subtask);
    }

    @Test
    @DisplayName("История сохраняет добавленные задачи в порядке их добавления")
    void addToHistoryTest() {
        final List<Intent> historyResult = historyManager.getHistory();

        assertNotNull(historyResult, "История не должна быть null");
        assertEquals(3, historyResult.size(), "Неверное количество элементов в истории");
        assertEquals(task, historyResult.get(0), "Добавленная task должна быть первой в истории");
        assertEquals(epic, historyResult.get(1), "Добавленный epic должен быть вторым в истории");
        assertEquals(subtask, historyResult.get(2), "Добавленная subtask должна быть третьей в истории");
    }

    @Test
    @DisplayName("При повторном добавлении задачи она перемещается в конец истории, без дубликатов")
    void addToHistoryShouldNotDuplicateTasksTest() {
        historyManager.addToHistory(task); // Добавляем уже существующую задачу task повторно
        final List<Intent> historyResult = historyManager.getHistory();

        assertEquals(3, historyResult.size(),
                "Повторное добавление задачи не должно увеличивать размер истории");
        assertEquals(epic, historyResult.get(0), "После перемещения task в конец, epic должен быть первым");
        assertEquals(subtask, historyResult.get(1),
                "После перемещения task в конец, subtask должен быть вторым");
        assertEquals(task, historyResult.get(2), "Повторно добавленная task должна быть в конце истории");
    }

    @Test
    @DisplayName("Удаление задачи из середины истории корректно пересвязывает список")
    void removeFromHistoryRemovesMiddleTaskCorrectlyTest() {
        // Удаляем средний элемент (epic)
        historyManager.removeFromHistory(epic.getId());
        final List<Intent> historyResult = historyManager.getHistory();

        assertEquals(2, historyResult.size(), "После удаления из истории должно остаться 2 элемента");
        assertEquals(task, historyResult.get(0), "После удаления эпика первым должен быть task");
        assertEquals(subtask, historyResult.get(1), "После удаления эпика вторым должен быть subtask");
        assertFalse(historyResult.contains(epic), "Эпик не должен присутствовать в истории после удаления");
    }

    @Test
    @DisplayName("Удаление первого элемента корректно пересвязывает список")
    void removeFirstNodeCorrectlyTest() {
        // удаляем первый элемент (task)
        historyManager.removeFromHistory(task.getId());
        final List<Intent> history = historyManager.getHistory();

        assertEquals(2, history.size(), "После удаления элемента в истории должно остаться 2 записи");
        assertEquals(epic, history.get(0), "После удаления head первым должен стать epic");
        assertEquals(subtask, history.get(1), "После удаления head вторым должен быть subtask");
        assertFalse(history.contains(task), "Удалённый task не должен присутствовать в истории");
    }

    @Test
    @DisplayName("Удаление последнего элемента корректно пересвязывает список")
    void removeLastNodeCorrectlyTest() {
        // удаляем последний элемент (subtask)
        historyManager.removeFromHistory(subtask.getId());
        final List<Intent> history = historyManager.getHistory();

        assertEquals(2, history.size(), "После удаления хвостового элемента должно остаться 2 записи");
        assertEquals(task, history.get(0), "Первым должен быть task");
        assertEquals(epic, history.get(1), "Вторым должен быть epic");
        assertFalse(history.contains(subtask), "Удалённый subtask не должен присутствовать в истории");
    }

    @Test
    @DisplayName("Удаление несуществующего элемента не должно вызывать ошибок")
    void shouldHandleRemovingNonExistingNodeTest() {
        assertDoesNotThrow(() -> historyManager.removeFromHistory(999),
                "Удаление элемента, которого нет в истории, не должно вызывать исключений");
    }

    @Test
    @DisplayName("Добавление null не должно изменять историю")
    void shouldIgnoreNullIntentTest() {
        int beforeSize = historyManager.getHistory().size();
        historyManager.addToHistory(null);
        List<Intent> history = historyManager.getHistory();

        assertEquals(beforeSize, history.size(), "История не должна измениться при добавлении null");
    }
}
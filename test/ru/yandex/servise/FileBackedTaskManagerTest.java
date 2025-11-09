package ru.yandex.servise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        manager = new FileBackedTaskManager(file);
    }

    @Test
    // одиночная задача сохраняется и корректно читается
    void shouldSaveAndLoadSingleTaskTest() {
        Task task = new Task("Task summary", "Task description", Status.NEW);
        manager.addTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = loaded.getTasks();

        assertEquals(1, tasks.size());
        assertEquals(task.getSummary(), tasks.get(0).getSummary());
        assertEquals(task.getDescription(), tasks.get(0).getDescription());
        assertEquals(task.getStatus(), tasks.get(0).getStatus());
    }

    @Test
    // эпик без сабтаск сохраняется и читается
    void shouldSaveAndLoadEpicWithoutSubtasksTest() {
        Epic epic = new Epic("Epic summary", "Epic description");
        manager.addEpic(epic);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        List<Epic> epics = loaded.getEpics();

        assertEquals(1, epics.size());
        assertEquals(epic.getSummary(), epics.get(0).getSummary());
        assertEquals(epic.getDescription(), epics.get(0).getDescription());
    }

    @Test
    // эпик и его сабтаски сохраняются и восстанавливаются
    void shouldSaveAndLoadEpicWithSubtasksTest() {
        Epic epic = manager.addEpic(new Epic("Epic summary", "Epic description"));
        Subtask sub1 = manager.addSubtask(new Subtask("Sub 1", "Desc 1", Status.NEW, epic.getId()));
        Subtask sub2 = manager.addSubtask(new Subtask("Sub 2", "Desc 2", Status.DONE, epic.getId()));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        List<Epic> epics = loaded.getEpics();
        List<Subtask> subtasks = loaded.getSubtasks();

        assertEquals(1, epics.size());
        assertEquals(2, subtasks.size());
        assertEquals(epic.getSummary(), epics.get(0).getSummary());
        assertTrue(subtasks.stream().anyMatch(s -> s.getSummary().equals("Sub 1")));
        assertTrue(subtasks.stream().anyMatch(s -> s.getSummary().equals("Sub 2")));
    }

    @Test
    // разные типы задач корректно записываются и восстанавливаются
    void shouldSaveAndLoadMultipleDifferentTasksTest() {
        Task task1 = manager.addTask(new Task("Task 1", "Desc 1", Status.NEW));
        Task task2 = manager.addTask(new Task("Task 2", "Desc 2", Status.DONE));
        Epic epic = manager.addEpic(new Epic("Epic 1", "Epic desc"));
        Subtask sub1 = manager.addSubtask(new Subtask("Sub 1", "Desc", Status.IN_PROGRESS, epic.getId()));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loaded.getTasks().size());
        assertEquals(1, loaded.getEpics().size());
        assertEquals(1, loaded.getSubtasks().size());

        assertEquals(task1.getSummary(), loaded.getTasks().get(0).getSummary());
    }

    @Test
    // пустой CSV с заголовком не вызывает ошибок
    void shouldCreateEmptyFileAndLoadEmptyManagerTest() throws IOException {
        Files.writeString(file.toPath(), "id,type,summary,status,description,epicId\n");

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
        assertTrue(loaded.getSubtasks().isEmpty());
    }

    @Test
    // полностью пустой файл корректно обрабатывается
    void shouldNotThrowExceptionOnEmptyFileTest() throws IOException {
        Files.writeString(file.toPath(), "");

        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file));
    }

}
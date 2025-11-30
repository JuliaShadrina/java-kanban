package ru.yandex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.http.LocalDateTimeAdapter;
import ru.yandex.model.Epic;
import ru.yandex.model.Subtask;
import ru.yandex.model.Task;
import ru.yandex.model.conctants.Status;
import ru.yandex.servise.InMemoryTaskManager;
import ru.yandex.servise.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    private int port;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        manager = new InMemoryTaskManager();

        // Используем разные порты для каждого теста
        port = findFreePort();
        taskServer = new HttpTaskServer(manager, port);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        client = HttpClient.newHttpClient();
        taskServer.start();

        // Даем серверу время на запуск
        Thread.sleep(100);
    }

    @AfterEach
    void shutDown() throws InterruptedException {
        taskServer.stop();
        // Даем серверу время на остановку
        Thread.sleep(100);
    }

    private int findFreePort() {
        return 8080 + (int)(Math.random() * 1000);
    }

    private URI createUri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    // Тесты для TasksHandler
    @Test
    void tasksHandler_GetEmptyTasksTest() throws IOException, InterruptedException {
        URI url = createUri("/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void tasksHandler_CreateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", Status.NEW);
        String taskJson = gson.toJson(task);

        URI url = createUri("/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task createdTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getSummary());
    }

    @Test
    void tasksHandler_GetTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", Status.NEW);
        Task createdTask = manager.addTask(task);

        URI url = createUri("/tasks/" + createdTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertEquals(createdTask.getId(), responseTask.getId());
        assertEquals("Test Task", responseTask.getSummary());
    }

    @Test
    void tasksHandler_GetTaskByIdNotFoundTest() throws IOException, InterruptedException {
        URI url = createUri("/tasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksHandler_UpdateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Original Task", "Original Description", Status.NEW);
        Task createdTask = manager.addTask(task);

        Task updatedTask = new Task(createdTask.getId(), "Updated Task", "Updated Description", Status.IN_PROGRESS);
        String updatedTaskJson = gson.toJson(updatedTask);

        URI url = createUri("/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task taskFromManager = manager.getTaskById(createdTask.getId());
        assertEquals("Updated Task", taskFromManager.getSummary());
        assertEquals(Status.IN_PROGRESS, taskFromManager.getStatus());
    }

    @Test
    void tasksHandler_DeleteTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", Status.NEW);
        Task createdTask = manager.addTask(task);

        URI url = createUri("/tasks/" + createdTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getTaskById(createdTask.getId()));
    }

    // Тесты для SubtasksHandler
    @Test
    void subtasksHandler_CreateSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        Epic createdEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", Status.NEW, createdEpic.getId());
        String subtaskJson = gson.toJson(subtask);

        URI url = createUri("/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getSubtasks().size());
    }

    @Test
    void subtasksHandler_GetSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        Epic createdEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", Status.NEW, createdEpic.getId());
        Subtask createdSubtask = manager.addSubtask(subtask);

        URI url = createUri("/subtasks/" + createdSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(createdSubtask.getId(), responseSubtask.getId());
        assertEquals("Test Subtask", responseSubtask.getSummary());
    }

    @Test
    void subtasksHandler_DeleteSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        Epic createdEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", Status.NEW, createdEpic.getId());
        Subtask createdSubtask = manager.addSubtask(subtask);

        URI url = createUri("/subtasks/" + createdSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getSubtaskById(createdSubtask.getId()));
    }

    // Тесты для EpicsHandler
    @Test
    void epicsHandler_CreateEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        String epicJson = gson.toJson(epic);

        URI url = createUri("/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getEpics().size());
    }

    @Test
    void epicsHandler_GetEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        Epic createdEpic = manager.addEpic(epic);

        URI url = createUri("/epics/" + createdEpic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(createdEpic.getId(), responseEpic.getId());
        assertEquals("Test Epic", responseEpic.getSummary());
    }

    @Test
    void epicsHandler_GetEpicSubtasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        Epic createdEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", Status.NEW, createdEpic.getId());
        manager.addSubtask(subtask);

        URI url = createUri("/epics/" + createdEpic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        assertTrue(responseBody.contains("Test Subtask"));
    }

    @Test
    void epicsHandler_DeleteEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        Epic createdEpic = manager.addEpic(epic);

        URI url = createUri("/epics/" + createdEpic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getEpicById(createdEpic.getId()));
    }

    // Тесты для HistoryHandler
    @Test
    void historyHandler_GetHistoryTest() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", Status.NEW);
        Task createdTask = manager.addTask(task);

        manager.getTaskById(createdTask.getId());

        URI url = createUri("/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        assertFalse(responseBody.isEmpty());
        assertTrue(responseBody.contains("Test Task"));
    }

    // Тесты для PrioritizedHandler
    @Test
    void prioritizedHandler_GetPrioritizedTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);

        manager.addTask(task1);
        manager.addTask(task2);

        URI url = createUri("/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        assertFalse(responseBody.isEmpty());
    }

    // Тесты обработки ошибок
    @Test
    void invalidEndpointTest() throws IOException, InterruptedException {
        URI url = createUri("/invalid");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void invalidMethodTest() throws IOException, InterruptedException {
        URI url = createUri("/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void malformedJsonTest() throws IOException, InterruptedException {
        String malformedJson = "{ invalid json }";

        URI url = createUri("/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(malformedJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

}
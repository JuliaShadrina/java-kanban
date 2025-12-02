package ru.yandex.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import ru.yandex.servise.InMemoryTaskManager;
import ru.yandex.servise.TaskManager;
import ru.yandex.HttpTaskServer;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HandlersTest {

    private TaskManager createTestManager() {
        return new InMemoryTaskManager();
    }

    private Gson createTestGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    private int findFreePort() {
        // Используем разные порты для каждого теста
        return 9000 + (int)(Math.random() * 1000);
    }

    @Test
    void tasksHandlerCreationTest() {
        TaskManager manager = createTestManager();
        Gson gson = createTestGson();

        TasksHandler handler = new TasksHandler(manager, gson);
        assertNotNull(handler);
    }

    @Test
    void subtasksHandlerCreationTest() {
        TaskManager manager = createTestManager();
        Gson gson = createTestGson();

        SubtasksHandler handler = new SubtasksHandler(manager, gson);
        assertNotNull(handler);
    }

    @Test
    void epicsHandlerCreationTest() {
        TaskManager manager = createTestManager();
        Gson gson = createTestGson();

        EpicsHandler handler = new EpicsHandler(manager, gson);
        assertNotNull(handler);
    }

    @Test
    void historyHandlerCreationTest() {
        TaskManager manager = createTestManager();
        Gson gson = createTestGson();

        HistoryHandler handler = new HistoryHandler(manager, gson);
        assertNotNull(handler);
    }

    @Test
    void prioritizedHandlerCreationTest() {
        TaskManager manager = createTestManager();
        Gson gson = createTestGson();

        PrioritizedHandler handler = new PrioritizedHandler(manager, gson);
        assertNotNull(handler);
    }

    @Test
    void httpTaskServerCreationTest() throws IOException, InterruptedException {
        int port = findFreePort();
        HttpTaskServer server = new HttpTaskServer(new InMemoryTaskManager(), port);
        assertNotNull(server);
        server.start();
        Thread.sleep(100); // Даем время на запуск
        server.stop();
        Thread.sleep(100); // Даем время на остановку
    }

    @Test
    void httpTaskServerWithCustomManagerTest() throws IOException, InterruptedException {
        int port = findFreePort();
        TaskManager manager = new InMemoryTaskManager();
        HttpTaskServer server = new HttpTaskServer(manager, port);
        assertNotNull(server);
        server.start();
        Thread.sleep(100); // Даем время на запуск
        server.stop();
        Thread.sleep(100); // Даем время на остановку
    }

    @Test
    void httpTaskServerWithCustomPortTest() throws IOException, InterruptedException {
        int port = findFreePort();
        TaskManager manager = new InMemoryTaskManager();
        HttpTaskServer server = new HttpTaskServer(manager, port);
        assertNotNull(server);
        assertEquals(port, server.getPort());
        server.start();
        Thread.sleep(100); // Даем время на запуск
        server.stop();
        Thread.sleep(100); // Даем время на остановку
    }

    @Test
    void localDateTimeAdapterTest() {
        LocalDateTimeAdapter adapter = new LocalDateTimeAdapter();
        assertNotNull(adapter);
    }

}
package ru.yandex;

import ru.yandex.http.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.servise.Managers;
import ru.yandex.servise.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int DEFAULT_PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;
    private final int port;

    // Конструктор по умолчанию для production
    public HttpTaskServer() throws IOException {
        this(Managers.getDefault(), DEFAULT_PORT);
    }

    // Конструктор для тестирования с произвольным менеджером
    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this(taskManager, DEFAULT_PORT);
    }

    // Конструктор для тестирования с произвольным портом
    public HttpTaskServer(TaskManager taskManager, int port) throws IOException {
        this.taskManager = taskManager;
        this.port = port;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        initializeHandlers();
    }

    private void initializeHandlers() {
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + port);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер: " + e.getMessage());
        }
    }

}
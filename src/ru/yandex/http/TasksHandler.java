package ru.yandex.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.model.Task;
import ru.yandex.servise.TaskManager;
import ru.yandex.servise.exception.IntersectionException;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendNotFound(exchange, "Метод не поддерживается");
            }
        } catch (Exception e) {
            sendInternalError(exchange, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            String response = gson.toJson(taskManager.getTasks());
            sendText(exchange, response);
        } else if (path.matches("/tasks/\\d+")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);

            Task task = taskManager.getTaskById(id);
            if (task != null) {
                String response = gson.toJson(task);
                sendText(exchange, response);
            } else {
                sendNotFound(exchange, "Задача с id=" + id + " не найдена");
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);

        try {
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonObject()) {
                Task task = gson.fromJson(jsonElement, Task.class);

                if (task.getId() == 0) {
                    // Создание новой задачи
                    Task createdTask = taskManager.addTask(task);
                    if (createdTask != null) {
                        String response = gson.toJson(createdTask);
                        sendCreated(exchange, response);
                    } else {
                        sendInternalError(exchange, "Не удалось создать задачу");
                    }
                } else {
                    // Обновление существующей задачи
                    Task updatedTask = taskManager.updateTask(task);
                    if (updatedTask != null) {
                        String response = gson.toJson(updatedTask);
                        sendText(exchange, response);
                    } else {
                        sendNotFound(exchange, "Задача с id=" + task.getId() + " не найдена");
                    }
                }
            }
        } catch (IntersectionException e) {
            sendHasOverlaps(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/tasks/\\d+")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);

            Task task = taskManager.getTaskById(id);
            if (task != null) {
                taskManager.removeTaskById(id);
                sendText(exchange, "{\"message\":\"Задача удалена\"}");
            } else {
                sendNotFound(exchange, "Задача с id=" + id + " не найдена");
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

}
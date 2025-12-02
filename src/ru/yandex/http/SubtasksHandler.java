package ru.yandex.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.model.Subtask;
import ru.yandex.servise.TaskManager;
import ru.yandex.servise.exception.IntersectionException;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
        if (path.equals("/subtasks")) {
            String response = gson.toJson(taskManager.getSubtasks());
            sendText(exchange, response);
        } else if (path.matches("/subtasks/\\d+")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);

            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                String response = gson.toJson(subtask);
                sendText(exchange, response);
            } else {
                sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
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
                Subtask subtask = gson.fromJson(jsonElement, Subtask.class);

                if (subtask.getId() == 0) {
                    // Создание новой подзадачи
                    Subtask createdSubtask = taskManager.addSubtask(subtask);
                    if (createdSubtask != null) {
                        String response = gson.toJson(createdSubtask);
                        sendCreated(exchange, response);
                    } else {
                        sendInternalError(exchange, "Не удалось создать подзадачу");
                    }
                } else {
                    // Обновление существующей подзадачи
                    Subtask updatedSubtask = taskManager.updateSubtask(subtask);
                    if (updatedSubtask != null) {
                        String response = gson.toJson(updatedSubtask);
                        sendText(exchange, response);
                    } else {
                        sendNotFound(exchange, "Подзадача с id=" + subtask.getId() + " не найдена");
                    }
                }
            }
        } catch (IntersectionException e) {
            sendHasOverlaps(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);

            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                taskManager.removeSubtaskById(id);
                sendText(exchange, "{\"message\":\"Подзадача удалена\"}");
            } else {
                sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }
}

package ru.yandex.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.model.Epic;
import ru.yandex.servise.TaskManager;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
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
        if (path.equals("/epics")) {
            String response = gson.toJson(taskManager.getEpics());
            sendText(exchange, response);
        } else if (path.matches("/epics/\\d+")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);

            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                String response = gson.toJson(epic);
                sendText(exchange, response);
            } else {
                sendNotFound(exchange, "Эпик с id=" + id + " не найдена");
            }
        } else if (path.matches("/epics/\\d+/subtasks")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);

            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                String response = gson.toJson(taskManager.getSubtasksByEpic(id));
                sendText(exchange, response);
            } else {
                sendNotFound(exchange, "Эпик с id=" + id + " не найдена");
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);

        JsonElement jsonElement = JsonParser.parseString(body);

        if (jsonElement.isJsonObject()) {
            Epic epic = gson.fromJson(jsonElement, Epic.class);

            if (epic.getId() == 0) {
                // Создание нового эпика
                Epic createdEpic = taskManager.addEpic(epic);
                if (createdEpic != null) {
                    String response = gson.toJson(createdEpic);
                    sendCreated(exchange, response);
                } else {
                    sendInternalError(exchange, "Не удалось создать эпик");
                }
            } else {
                // Обновление существующего эпика
                Epic updatedEpic = taskManager.updateEpic(epic);
                if (updatedEpic != null) {
                    String response = gson.toJson(updatedEpic);
                    sendText(exchange, response);
                } else {
                    sendNotFound(exchange, "Эпик с id=" + epic.getId() + " не найден");
                }
            }
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);

            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                taskManager.removeEpicById(id);
                sendText(exchange, "{\"message\":\"Эпик удален\"}");
            } else {
                sendNotFound(exchange, "Эпик с id=" + id + " не найден");
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }
}

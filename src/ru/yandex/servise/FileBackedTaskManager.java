package ru.yandex.servise;

import ru.yandex.model.*;
import ru.yandex.model.conctants.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String HEADER = "id,type,summary,status,description,epicId";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        File file = new File("tasks.csv");

        // Создаём менеджер и добавляем задачи
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпика1");
        Epic epic2 = new Epic("Эпик2", "ОписаниеЭпика2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Task task1 = new Task("Задача1", "ОписаниеЗадачи1", Status.NEW);
        Task task2 = new Task("Задача2", "ОписаниеЗадачи2", Status.IN_PROGRESS);
        manager.addTask(task1);
        manager.addTask(task2);

        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадачи1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадачи2", Status.DONE, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача3", "ОписаниеПодзадачи3", Status.IN_PROGRESS, epic2.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        // Загружаем новый менеджер из файла
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);

        // Проверяем, что данные совпадают
        boolean epicsEqual = manager.getEpics().equals(newManager.getEpics());
        boolean tasksEqual = manager.getTasks().equals(newManager.getTasks());
        boolean subtasksEqual = manager.getSubtasks().equals(newManager.getSubtasks());

        System.out.println("Эпики совпадают: " + epicsEqual);
        System.out.println("Задачи совпадают: " + tasksEqual);
        System.out.println("Подзадачи совпадают: " + subtasksEqual);

        // Пример истории просмотров
        newManager.getEpicById(epic1.getId());
        newManager.getTaskById(task2.getId());
        newManager.getSubtaskById(subtask1.getId());
        List<Intent> history = newManager.getHistory();
        System.out.println("История просмотров нового менеджера: " + history);
    }

    /* ------------------ Загрузка из файла ------------------ */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> lines = reader.lines().toList();
            if (lines.isEmpty() || lines.size() == 1) {
                return manager; // только заголовок
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                Intent intent = fromString(line);
                if (intent instanceof Task task) {
                    manager.addWithoutSave(task);
                } else if (intent instanceof Epic epic) {
                    manager.addWithoutSave(epic);
                } else if (intent instanceof Subtask subtask) {
                    manager.addWithoutSave(subtask);
                }
            }

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return manager;
    }

    /* ------------------ Сохранение ------------------ */
    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения в файл: " + e.getMessage());
        }
    }

    /* ------------------ Преобразования ------------------ */

    private static Intent fromString(String line) {
        String[] fields = line.split(",");
        int id = Integer.parseInt(fields[0]);
        IntentType type = IntentType.valueOf(fields[1]);
        String summary = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK -> {
                return new Task(id, summary, description, status);
            }
            case EPIC -> {
                return new Epic(id, summary, description, status);
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, summary, description, status, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private String toString(Intent intent) {
        String base = intent.getId() + "," +
                intent.getClass().getSimpleName().toUpperCase().replace("TASK", "TASK").replace("EPIC", "EPIC").replace("SUBTASK", "SUBTASK") + "," +
                intent.getSummary() + "," +
                intent.getStatus() + "," +
                intent.getDescription();

        if (intent instanceof Subtask subtask) {
            base += "," + subtask.getEpicId();
        }

        return base;
    }

    /* ------------------ Методы с автосохранением ------------------ */

    @Override
    public Epic addEpic(Epic newEpic) {
        Epic epic = super.addEpic(newEpic);
        save();
        return epic;
    }

    @Override
    public Task addTask(Task newTask) {
        Task task = super.addTask(newTask);
        save();
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask newSubtask) {
        Subtask subtask = super.addSubtask(newSubtask);
        save();
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic updateEpic) {
        Epic epic = super.updateEpic(updateEpic);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task updateTask) {
        Task task = super.updateTask(updateTask);
        save();
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask updateSubtask) {
        Subtask subtask = super.updateSubtask(updateSubtask);
        save();
        return subtask;
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    /* --- Вспомогательные методы без автосохранения (для загрузки из файла) --- */
    private void addWithoutSave(Intent intent) {
        if (intent instanceof Task task) {
            super.addTask(task);
        } else if (intent instanceof Epic epic) {
            super.addEpic(epic);
        } else if (intent instanceof Subtask subtask) {
            super.addSubtask(subtask);
        }
    }

}
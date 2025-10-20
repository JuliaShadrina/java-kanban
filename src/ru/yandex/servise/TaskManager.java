package ru.yandex.servise;

import ru.yandex.model.*;

import java.util.List;

public interface TaskManager {

    // Получение списков всех типов задач
    public List<Epic> getEpics(); // список эпиков

    public List<Task> getTasks(); // список таск

    public List<Subtask> getSubtasks(); // список сабтаск

    // Добавление в систему каждого типа задач,
    public Epic addEpic(Epic newEpic);

    public Task addTask(Task newTask);

    public Subtask addSubtask(Subtask newSubtask);

    // Список сабтаск эпика
    public List<Subtask> getSubtasksByEpic(int id);

    // Обновление каждого типа задач
    public Epic updateEpic(Epic updateEpic);

    public Task updateTask(Task updateTask);

    public Subtask updateSubtask(Subtask updateSubtask);

    // Получение каждого типа задач по id
    public Epic getEpicById(int id);

    public Task getTaskById(int id);

    public Subtask getSubtaskById(int id);

    // Удаление каждого типа задач по id
    public void removeEpicById(int id);

    public void removeTaskById(int id);

    public void removeSubtaskById(int id);

    // Удаление всех задач каждого типа
    public void deleteAllEpics();

    public void deleteAllTasks();

    public void deleteAllSubtasks();

    List<Intent> getHistory();

}
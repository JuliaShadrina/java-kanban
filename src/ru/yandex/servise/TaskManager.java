package ru.yandex.servise;

import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public int generationId = 0;

    // Получение списков всех типов задач
    public HashMap<Integer, Epic> getEpics() { // список эпиков
        if (epics.size() != 0) {
            return epics;
        } else {
            return new HashMap<>();
        }
    }

    public HashMap<Integer, Task> getTasks() { // список таск
        if (tasks.size() != 0) {
            return tasks;
        } else {
            return new HashMap<>();
        }
    }

    public HashMap<Integer, Subtask> getSubtasks() { // список сабтаск
        if (subtasks.size() != 0) {
            return subtasks;
        } else {
            return new HashMap<>();
        }
    }
    // Проверка, что новая задача любого типа не пустая и что Id не совпадает с имеющимися
    public boolean isNewIntent(Intent newIntent) {
        if (newIntent != null && !tasks.containsKey(newIntent.getId()) && !subtasks.containsKey(newIntent.getId())
                && !epics.containsKey(newIntent.getId())){
            return true;
        } else {
            return false;
        }
    }

    // Обновление статуса эпика
    private void updateEpicStatus(Epic epic) {
        boolean isNew = true;
        boolean isDone = true;

        if (epic.getSubtasksIds().size() == 0) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (Integer epicSubtask : epic.getSubtasksIds()) {
            Status status = subtasks.get(epicSubtask).getStatus();
            if (status != Status.NEW) {
                isNew = false;
            }
            if (status != Status.DONE) {
                isDone = false;
            }
        }
        if (isNew) {
            epic.setStatus(Status.NEW);
        } else if (isDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // Добавление в систему каждого типа задач,
    // при успешном добавлении возвращаем задачу, если контроли не пройдены и добавить не удалось - null
    public Epic addEpic(Epic newEpic) {
        if (isNewIntent(newEpic)) {
            if (newEpic.getId() == 0) {
                int epicId = ++generationId;
                newEpic.setId(epicId);
            }
            epics.put(newEpic.getId(), newEpic);
            updateEpicStatus(newEpic);
            return newEpic;
        } else {
            return null;
        }
    }

    public Task addTask(Task newTask) {
        if (isNewIntent(newTask)) {
            if (newTask.getId() == 0) {
                int taskId = ++generationId;
                newTask.setId(taskId);
            }
            tasks.put(newTask.getId(), newTask);
            return newTask;
        } else {
            return null;
        }
    }

    public Subtask addSubtask(Subtask newSubtask) {
        if (isNewIntent(newSubtask)) {
            if (newSubtask.getId() == 0) {
                int subtaskId = ++generationId;
                newSubtask.setId(subtaskId);
            }
            Epic epic = epics.get(newSubtask.getEpicId());
            if (epic != null) {
                subtasks.put(newSubtask.getId(), newSubtask);
                epic.setSubtasksIds(newSubtask.getId());
                updateEpicStatus(epic);
            }
            return newSubtask;
        } else {
            return null;
        }
    }

    // Список сабтаск эпика
    public ArrayList<Subtask> getSubtasksByEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
            for (int i = 0; i < epic.getSubtasksIds().size(); i++) {
                subtasksByEpic.add(subtasks.get(epic.getSubtasksIds().get(i)));
            }
            return subtasksByEpic;
        } else {
            return new ArrayList<>();
        }
    }

    // Обновление каждого типа задач
    // при успешном обновлении возвращаем задачу, если контроли не пройдены и обновить не удалось - null
    public Epic updateEpic(Epic updateEpic) {
        if (updateEpic != null && epics.containsKey(updateEpic.getId())) {
            int id = updateEpic.getId();
            epics.put(id, updateEpic);
            updateEpicStatus(updateEpic);
            return updateEpic;
        } else {
            return null;
        }
    }

    public Task updateTask(Task updateTask) {
        if (updateTask != null && tasks.containsKey(updateTask.getId())) {
            int id = updateTask.getId();
            tasks.put(id, updateTask);
            return updateTask;
        } else {
            return null;
        }
    }

    public Subtask updateSubtask(Subtask updateSubtask) {
        if (updateSubtask != null && subtasks.containsKey(updateSubtask.getId())) {
            int id = updateSubtask.getId();
            Subtask existingSubtask = subtasks.get(id);
            int oldEpicId = existingSubtask.getEpicId();
            int newEpicId = updateSubtask.getEpicId();
            // Если epicId изменился, обновляем связи
            if (oldEpicId != newEpicId) {
                // Убираем сабтаску из старого эпика
                Epic oldEpic = epics.get(oldEpicId);
                if (oldEpic != null) {
                    oldEpic.getSubtasksIds().remove(Integer.valueOf(id)); // Удаляем сабтаску
                    updateEpicStatus(oldEpic); // Обновляем статус старого эпика
                }
                // Привязываем сабтаску к новому эпику
                Epic newEpic = epics.get(newEpicId);
                if (newEpic != null) {
                    newEpic.setSubtasksIds(id); // Добавляем сабтаску в новый эпик
                    updateEpicStatus(newEpic); // Обновляем статус нового эпика
                }
            }
            // Обновляем сабтаску
            subtasks.put(id, updateSubtask);
            return updateSubtask;
        } else {
            return null;
        }
    }

    // Получение каждого типа задач по id
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null && epics.containsKey(id)) {
            return epic;
        } else {
            return null;
        }
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null && tasks.containsKey(id)) {
            return task;
        } else {
            return null;
        }
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null && subtasks.containsKey(id)) {
            return subtask;
        } else {
            return null;
        }
    }

    // Удаление каждого типа задач по id
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtaskIds = epics.get(id).getSubtasksIds();
            for (Integer subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            if (epics.containsKey(epicId)) {
                Epic epic = epics.get(epicId);
                ArrayList<Integer> subtaskIds = epic.getSubtasksIds();
                subtaskIds.remove((Integer) id);
                updateEpicStatus(epic);
            }
            subtasks.remove(id);
        }
    }

    // Удаление всех задач каждого типа
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            int epicId = epic.getId();
            ArrayList<Integer> subtaskIds = epics.get(epicId).getSubtasksIds();
            for (Integer subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();
    }

    public void deleteAllTasks() {
        tasks.clear();
    }
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }
}

package ru.yandex.servise;

import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generationId = 0;
    private HistoryManager historyManager = Managers.getDefaultHistory(); // история полная история просмотров

    // Получение списков всех типов задач
    @Override
    public List<Epic> getEpics() { // список эпиков
        if (epics.size() != 0) {
            return new ArrayList<>(epics.values());
        } else {
            return new ArrayList<>();
        }
    }
    @Override
    public List<Task> getTasks() { // список таск
        if (tasks.size() != 0) {
            return new ArrayList<>(tasks.values());
        } else {
            return new ArrayList<>();
        }
    }
    @Override
    public List<Subtask> getSubtasks() { // список сабтаск
        if (subtasks.size() != 0) {
            return new ArrayList<>(subtasks.values());
        } else {
            return new ArrayList<>();
        }
    }

    // Добавление в систему каждого типа задач,
    // при успешном добавлении возвращаем задачу, если контроли не пройдены и добавить не удалось - null
    @Override
    public Epic addEpic(Epic newEpic) {
        if (isNewIntent(newEpic)) {
            if (newEpic.getId() == 0) {
                newEpic.setId(generateId());
            }
            epics.put(newEpic.getId(), newEpic);
            updateEpicStatus(newEpic);
            return newEpic;
        } else {
            return null;
        }
    }
    @Override
    public Task addTask(Task newTask) {
        if (isNewIntent(newTask)) {
            if (newTask.getId() == 0) {
                newTask.setId(generateId());
            }
            tasks.put(newTask.getId(), newTask);
            return newTask;
        } else {
            return null;
        }
    }
    @Override
    public Subtask addSubtask(Subtask newSubtask) {
        if (isNewIntent(newSubtask)) {
            if (newSubtask.getId() == 0) {
                newSubtask.setId(generateId());;
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
    @Override
    public List<Subtask> getSubtasksByEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Subtask> subtasksByEpic = new ArrayList<>();
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
    @Override
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
    @Override
    public Task updateTask(Task updateTask) {
        if (updateTask != null && tasks.containsKey(updateTask.getId())) {
            int id = updateTask.getId();
            tasks.put(id, updateTask);
            return updateTask;
        } else {
            return null;
        }
    }
    @Override
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
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null && epics.containsKey(id)) {
            historyManager.addToHistory(epic);
            return epic;
        } else {
            return null;
        }
    }
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null && tasks.containsKey(id)) {
            historyManager.addToHistory(task);
            return task;
        } else {
            return null;
        }
    }
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null && subtasks.containsKey(id)) {
            historyManager.addToHistory(subtask);
            return subtask;
        } else {
            return null;
        }
    }

    // Удаление каждого типа задач по id
    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            List<Integer> subtaskIds = epics.get(id).getSubtasksIds();
            for (Integer subtaskId : subtaskIds) {
                historyManager.removeFromHistory(subtaskId); // сначала из истории удаляем каждую сабтаску
                subtasks.remove(subtaskId);
            }
            historyManager.removeFromHistory(id); // сначала из истории удаляем эпик
            epics.remove(id);
        }
    }
    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.removeFromHistory(id); // сначала из истории удаляем каждую таску
            tasks.remove(id);
        }
    }
    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            if (epics.containsKey(epicId)) {
                Epic epic = epics.get(epicId);
                ArrayList<Integer> subtaskIds = epic.getSubtasksIds();
                subtaskIds.remove((Integer) id);
                updateEpicStatus(epic);
            }
            historyManager.removeFromHistory(id); // сначала из истории удаляем сабтаску
            subtasks.remove(id);
        }
    }

    // Удаление всех задач каждого типа
    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            int epicId = epic.getId();
            List<Integer> subtaskIds = epics.get(epicId).getSubtasksIds(); // получаем список id сабтаск каждого эпика
            for (Integer subtaskId : subtaskIds) {
                historyManager.removeFromHistory(subtaskId); // сначала из истории удаляем каждую сабтаску
                subtasks.remove(subtaskId); // потом из системы удаляем
            }
            historyManager.removeFromHistory(epicId); // из истории удаляем каждый эпик
        }
        epics.clear();
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            int taskId = task.getId();
            historyManager.removeFromHistory(taskId); // сначала из истории удаляем каждую таску
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear(); // у всех эпиков очищаем список ссылок на сабтаски
            updateEpicStatus(epic);
        }
        for (Subtask subtask : subtasks.values()) {
            int subtaskId = subtask.getId();
            historyManager.removeFromHistory(subtaskId); // сначала из истории удаляем каждую сабтаску
        }
        subtasks.clear();
    }

    // Возвращает историю - полный список просмотренных задач без дубликатов
    @Override
    public List<Intent> getHistory() {
        return historyManager.getHistory();
    }

    // Проверка, что новая задача любого типа не пустая и что Id не совпадает с имеющимися
    private boolean isNewIntent(Intent newIntent) {
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

    // Генерация id
    private int generateId() {
        return ++generationId;
    }

}
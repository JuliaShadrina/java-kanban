package ru.yandex;

import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;
import ru.yandex.servise.*;

public class Main {

    public static void main(String[] args){
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        // Создаём эпики
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпика1");
        Epic epic2 = new Epic("Эпик2", "ОписаниеЭпика2");
        Epic epic3 = new Epic("Эпик3", "ОписаниеЭпика3");
        Epic epic4 = new Epic("Эпик4", "ОписаниеЭпика4");
        // добавляем в систему
        System.out.println("Добавленные эпики:");
        System.out.println(taskManager.addEpic(epic1));
        System.out.println(taskManager.addEpic(epic2));
        System.out.println(taskManager.addEpic(epic3));
        // печатаем список эпиков
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        // Создаём таски
        Task task1 = new Task("Задача1", "ОписаниеЗадачи1", Status.NEW);
        Task task2 = new Task("Задача2", "ОписаниеЗадачи2", Status.IN_PROGRESS);
        // добавляем в систему
        System.out.println("Добавленные таски:");
        System.out.println(taskManager.addTask(task1));
        System.out.println(taskManager.addTask(task2));
        // печатаем список таск
        System.out.println("Список таск");
        System.out.println(taskManager.getTasks());
        // Создаём сабтаски
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадачи1", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадачи2", Status.NEW, 1);
        Subtask subtask3 = new Subtask("Подзадача3", "ОписаниеПодзадачи3", Status.IN_PROGRESS, 2);
        Subtask subtask4 = new Subtask("Подзадача4", "ОписаниеПодзадачи4", Status.DONE, 3);
        // добаляем к эпикам
        System.out.println("Добавленные сабтаски:");
        System.out.println(taskManager.addSubtask(subtask1));
        System.out.println(taskManager.addSubtask(subtask2));
        System.out.println(taskManager.addSubtask(subtask3));
        System.out.println(taskManager.addSubtask(subtask4));
        // печатаем список сабтаск
        System.out.println("Список сабтаск:");
        System.out.println(taskManager.getSubtasks());
        System.out.println("Эпик по id:");
        System.out.println(taskManager.getEpicById(1));
        System.out.println("Таска по id:");
        System.out.println(taskManager.getTaskById(4));
        System.out.println("Сабтаска по id:");
        System.out.println(taskManager.getSubtaskById(6));
        // ещё раз список эпиков с обновлёнными статусами
        System.out.println("Ещё раз список эпиков с новыми статусами:");
        System.out.println(taskManager.getEpics());
        // печатаем список сабтаск по эпикам
        System.out.println("Сабтаски каждого эпика:");
        System.out.println(taskManager.getSubtasksByEpic(1));
        System.out.println(taskManager.getSubtasksByEpic(2));
        System.out.println(taskManager.getSubtasksByEpic(3));
        // обновляем каждый тип задач
        System.out.println("Обновление задач каждого типа:");
        Epic epic4upd = new Epic(4, "Эпик4обновлённый", "ОписаниеЭпика4обновлённое");
        System.out.println(taskManager.updateEpic(epic4upd));
        Task task1upd = new Task(4,"Задача1обновлённая", "ОписаниеЗадачи1обновлённое", Status.DONE);
        System.out.println(taskManager.updateTask(task1upd));
        Subtask subtask4upd = new Subtask(9,"Подзадача4обновлённая", "ОписаниеПодзадачи4обновлённое", Status.DONE, 2);
        System.out.println(taskManager.updateSubtask(subtask4upd));
        // печатаем список сабтаск по эпикам
        System.out.println("Сабтаски каждого эпика:");
        System.out.println(taskManager.getSubtasksByEpic(1));
        System.out.println(taskManager.getSubtasksByEpic(2));
        System.out.println(taskManager.getSubtasksByEpic(3));
        System.out.println("Ещё раз список эпиков с новыми статусами:");
        System.out.println(taskManager.getEpics());
        System.out.println("Удаляем эпик по id = 1:");
        taskManager.removeEpicById(1);
        System.out.println("Удаляем таску по id = 4:");
        taskManager.removeTaskById(4);
        System.out.println("Удаляем сабтаску по id = 6:");
        taskManager.removeSubtaskById(6);
        // удаляем все эпики
        //taskManager.deleteAllEpics();
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        // удаляем все таски
        //taskManager.deleteAllTasks();
        System.out.println("Список таск:");
        System.out.println(taskManager.getTasks());
        // удаляем все сабтаски
        //taskManager.deleteAllSubtasks();
        System.out.println("Список сабтаск:");
        System.out.println(taskManager.getSubtasks());
        // удаляем все эпики
    }
}

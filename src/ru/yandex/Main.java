package ru.yandex;

import ru.yandex.model.*;
import ru.yandex.model.conctants.Status;
import ru.yandex.servise.*;

public class Main {

    public static void main(String[] args){
        System.out.println("Поехали!");
        TaskManager inMemoryTaskManager = Managers.getDefault();
        // Создаём эпики
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпика1");
        Epic epic2 = new Epic("Эпик2", "ОписаниеЭпика2");
        Epic epic3 = new Epic("Эпик3", "ОписаниеЭпика3");
        Epic epic4 = new Epic("Эпик4", "ОписаниеЭпика4");
        // добавляем в систему
        System.out.println("Добавленные эпики:");
        System.out.println(inMemoryTaskManager.addEpic(epic1));
        System.out.println(inMemoryTaskManager.addEpic(epic2));
        System.out.println(inMemoryTaskManager.addEpic(epic3));
        System.out.println(inMemoryTaskManager.addEpic(epic4));
        // печатаем список эпиков
        System.out.println("Список эпиков:");
        System.out.println(inMemoryTaskManager.getEpics());
        // Создаём таски
        Task task1 = new Task("Задача1", "ОписаниеЗадачи1", Status.NEW);
        Task task2 = new Task("Задача2", "ОписаниеЗадачи2", Status.IN_PROGRESS);
        // добавляем в систему
        System.out.println("Добавленные таски:");
        System.out.println(inMemoryTaskManager.addTask(task1));
        System.out.println(inMemoryTaskManager.addTask(task2));
        // печатаем список таск
        System.out.println("Список таск");
        System.out.println(inMemoryTaskManager.getTasks());
        // Создаём сабтаски
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадачи1", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадачи2", Status.NEW, 1);
        Subtask subtask3 = new Subtask("Подзадача3", "ОписаниеПодзадачи3", Status.IN_PROGRESS, 2);
        Subtask subtask4 = new Subtask("Подзадача4", "ОписаниеПодзадачи4", Status.DONE, 3);
        // добаляем к эпикам
        System.out.println("Добавленные сабтаски:");
        System.out.println(inMemoryTaskManager.addSubtask(subtask1));
        System.out.println(inMemoryTaskManager.addSubtask(subtask2));
        System.out.println(inMemoryTaskManager.addSubtask(subtask3));
        System.out.println(inMemoryTaskManager.addSubtask(subtask4));
        // печатаем список сабтаск
        System.out.println("Список сабтаск:");
        System.out.println(inMemoryTaskManager.getSubtasks());
        // получаем объекты по id
        System.out.println("Эпик по id = 1:");
        System.out.println(inMemoryTaskManager.getEpicById(1));
        System.out.println("История просмотров:");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("Таска по id = 5:");
        System.out.println(inMemoryTaskManager.getTaskById(5));
        System.out.println("История просмотров:");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("Сабтаска по id = 9:");
        System.out.println(inMemoryTaskManager.getSubtaskById(9));
        System.out.println("История просмотров:");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("ещё раз Таска по id = 5:");
        System.out.println(inMemoryTaskManager.getTaskById(5));
        System.out.println("История просмотров без дубликата Таски с id = 5");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("ещё раз Эпик по id = 1:");
        System.out.println(inMemoryTaskManager.getEpicById(1));
        System.out.println("История просмотров без дубликата Эпика с id = 1");
        System.out.println(inMemoryTaskManager.getHistory());
        // ещё раз список эпиков с обновлёнными статусами
        System.out.println("Ещё раз список эпиков с новыми статусами:");
        System.out.println(inMemoryTaskManager.getEpics());
        // печатаем список сабтаск по эпикам
        System.out.println("Сабтаски каждого эпика:");
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(1));
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(2));
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(3));
        // обновляем каждый тип задач
        System.out.println("Обновление задач каждого типа:");
        Epic epic4upd = new Epic(4, "Эпик4обновлённый", "ОписаниеЭпика4обновлённое");
        System.out.println(inMemoryTaskManager.updateEpic(epic4upd));
        Task task1upd = new Task(5,"Задача1обновлённая", "ОписаниеЗадачи1обновлённое", Status.DONE);
        System.out.println(inMemoryTaskManager.updateTask(task1upd));
        Subtask subtask4upd = new Subtask(9,"Подзадача4обновлённая", "ОписаниеПодзадачи4обновлённое", Status.DONE, 2);
        System.out.println(inMemoryTaskManager.updateSubtask(subtask4upd));
        // печатаем список сабтаск по эпикам
        System.out.println("Сабтаски каждого эпика:");
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(1));
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(2));
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(3));
        System.out.println("Ещё раз список эпиков с новыми статусами:");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Удаляем эпик по id = 3:");
        inMemoryTaskManager.removeEpicById(3);
        System.out.println("Удаляем таску по id = 6:");
        inMemoryTaskManager.removeTaskById(6);
        System.out.println("Удаляем сабтаску по id = 8:");
        inMemoryTaskManager.removeSubtaskById(8);
        System.out.println("Удаляем все таски");
        inMemoryTaskManager.deleteAllTasks();
        System.out.println("Список таск:");
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println("Таски удалились из истории тоже:");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("Удаляем все сабтаски");
        inMemoryTaskManager.deleteAllSubtasks();
        System.out.println("Список сабтаск:");
        System.out.println(inMemoryTaskManager.getSubtasks());
        System.out.println("Сабтаски удалились из истории тоже:");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("Удаляем все эпики");
        inMemoryTaskManager.deleteAllEpics();
        System.out.println("Список эпиков:");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("История пуста");
        System.out.println(inMemoryTaskManager.getHistory());
        // удаляем все эпики
    }
}

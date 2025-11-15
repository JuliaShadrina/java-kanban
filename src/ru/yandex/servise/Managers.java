package ru.yandex.servise;

import java.io.File;

public class Managers { // фабрика менеджеров

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}

package ru.yandex.servise;

import ru.yandex.model.Intent;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Intent> history = new ArrayList<>(); // история - последние 10 просмотренных задач

    @Override
    public List<Intent> getHistory() {
        return history;
    }

    // добавление задачи любого типа в историю просмотров
    // если добавление успешно, возвращаем саму же задачу, если добавить не удалось - null
    @Override
    public Intent add(Intent intent) {
            if ((intent == null) || (history.size() > 10)) {
                return null;
            } else { // контроли пройдены: задача не пустая и история не длиннее 10
                if (history.size() == 10) { // если все 10 эл-тов уже заполнены, убираем самый старый
                    history.remove(0);
                }
                history.add(intent);
                return intent;
            }
    }
}

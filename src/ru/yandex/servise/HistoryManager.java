package ru.yandex.servise;

import ru.yandex.model.Intent;

import java.util.List;

public interface HistoryManager {

    public List<Intent> getHistory();

    public Intent add(Intent intent);
}

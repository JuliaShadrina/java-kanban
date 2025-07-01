package ru.yandex.model;

import ru.yandex.model.conctants.Status;
import ru.yandex.model.conctants.Type;
import java.util.ArrayList;

public class Epic extends Intent {
    protected ArrayList<Integer> subtasksIds = new ArrayList<>(); // храним список сабтаск

    public Epic(String summary, String description) {
        super(Type.EPIC, summary, description);
    }

    public Epic(String summary, String description, Status status) {
        super(Type.EPIC, summary, description, status);
    }

    public Epic(int id, String summary, String description) {
        super(id, Type.EPIC, summary, description);
    }

    public Epic(int id, String summary, String description, Status status, ArrayList<Integer> subtasksIds) {
        super(id, Type.EPIC, summary, description, status);
        this.subtasksIds = subtasksIds;
    }

    public ArrayList<Integer> getSubtasksIds() { // получаем список сабтаск
        return subtasksIds;
    }

    public void setSubtasksIds(int id) { // добавляем одну сабтаску
        subtasksIds.add(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", summary='" + getSummary() + '\'' +
                ", subtasks quantity=" + subtasksIds.size() +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' + '}';
    }
}

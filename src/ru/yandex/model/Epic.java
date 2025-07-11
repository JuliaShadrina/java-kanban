package ru.yandex.model;

import ru.yandex.model.conctants.Status;
import java.util.ArrayList;

public class Epic extends Intent {
    private ArrayList<Integer> subtasksIds = new ArrayList<>(); // храним список сабтаск

    public Epic(String summary, String description) {
        super(summary, description);
    }

    public Epic(String summary, String description, Status status) {
        super(summary, description, status);
    }

    public Epic(int id, String summary, String description, Status status) {
        super(id, summary, description, status);
    }

    public Epic(int id, String summary, String description) {
        super(id, summary, description);
    }

    public Epic(int id, String summary, String description, Status status, ArrayList<Integer> subtasksIds) {
        super(id, summary, description, status);
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

package ru.yandex.model;

import ru.yandex.model.conctants.Status;

public class Task extends Intent {

    public Task(String summary, String description, Status status) {
        super(summary, description, status);
    }

    public Task(int id, String summary, String description, Status status) {
        super(id, summary, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", summary='" + getSummary() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' + '}';
    }
}

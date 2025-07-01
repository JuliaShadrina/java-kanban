package ru.yandex.model;

import ru.yandex.model.conctants.Status;
import ru.yandex.model.conctants.Type;

public class Subtask extends Intent {
    protected int epicId; // храним к какому эпику относится

    public Subtask(String summary, String description) {
        super(Type.SUBTASK, summary, description);
    }

    public Subtask(String summary, String description, Status status, int epicId) {
        super(Type.SUBTASK, summary, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String summary, String description, Status status, int epicId) {
        super(id, Type.SUBTASK, summary, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", summary='" + getSummary() + '\'' +
                ", epicId=" + getEpicId() +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' + '}';
    }
}

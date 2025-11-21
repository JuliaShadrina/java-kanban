package ru.yandex.model;

import ru.yandex.model.conctants.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Intent {
    private ArrayList<Integer> subtasksIds = new ArrayList<>(); // храним список сабтаск
    private LocalDateTime endTime; // расчётное

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

    public void calculateTimeAndDuration(List<Subtask> subtasks) {

        if (subtasks.isEmpty()) {
            setDuration(0);
            setStartTime(null);
            endTime = null;
            return;
        }

        int totalDuration = 0;
        LocalDateTime earliest = null;
        LocalDateTime latest = null;

        for (Subtask s : subtasks) {

            if (s.getStartTime() != null) {

                if (earliest == null || s.getStartTime().isBefore(earliest)) {
                    earliest = s.getStartTime();
                }

                LocalDateTime subEnd = s.getEndTime();
                if (subEnd != null && (latest == null || subEnd.isAfter(latest))) {
                    latest = subEnd;
                }
            }

            totalDuration += s.getDuration();
        }

        setDuration(totalDuration);
        setStartTime(earliest);
        endTime = latest;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", summary='" + getSummary() + '\'' +
                ", subtasks quantity=" + subtasksIds.size() +
                ", description='" + getDescription() + '\'' +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + endTime +
                ", status='" + getStatus() + '\'' + '}';
    }

}

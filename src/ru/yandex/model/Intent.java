package ru.yandex.model;

import ru.yandex.model.conctants.Status;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Intent {
    private int id;
    private String summary;
    private String description;
    private Status status;
    private int duration;
    private LocalDateTime startTime;

    public Intent(String summary, String description) {
        this.summary = summary;
        this.description = description;
        this.status = Status.NEW;
    }

    public Intent(int id, String summary, String description) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.status = Status.NEW;
    }

    public Intent(String summary, String description, Status status) {
        this.summary = summary;
        this.description = description;
        this.status = status;
    }

    public Intent(int id, String summary, String description, Status status) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) return null;
        return startTime.plusMinutes(duration);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intent intent = (Intent) o;
        return id == intent.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}


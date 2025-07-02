package ru.yandex.model;

import ru.yandex.model.conctants.Status;

import java.util.Objects;

public abstract class Intent {
    private int id;
    private String summary;
    private String description;
    private Status status;

    public Intent(String summary, String description) {
        this.summary = summary;
        this.description = description;
        this.status = status;
    }

    public Intent(int id, String summary, String description) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.status = status;
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
        return Objects.equals(summary, intent.summary) &&
                Objects.equals(description, intent.description) &&
                Objects.equals(id, intent.id) &&
                Objects.equals(status, intent.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary, description, id, status);
    }
}

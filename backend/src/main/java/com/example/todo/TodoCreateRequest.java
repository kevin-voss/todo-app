package com.example.todo;

import jakarta.validation.constraints.NotBlank;

public class TodoCreateRequest {

    @NotBlank(message = "title must not be blank")
    private String title;

    private Boolean completed;

    public TodoCreateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}

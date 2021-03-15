package com.taskersolutions.tasker_todolist.Model;

public class ToDoModel {

    private int id;
    // status is the status of the checkbox. using 1/0 because boolean is not supported
    private int status;
    private String task;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}

package com.example.project1.model;

import java.util.List;

public class Task {

private  String title,description,priority;
private String start_date,end_date;
private List<Integer> user_ids;
private int owner_id,status=0;

//    public Task(String title, String description,  Date start_date, Date end_date,String priority, List<Integer> user_ids, int owner_id, int status) {
public Task(String title, String description,String start_date, String end_date,String priority, List<Integer> user_ids, int owner_id, int status) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.start_date = start_date;
        this.end_date = end_date;
        this.user_ids = user_ids;
        this.owner_id = owner_id;
        this.status = status;

    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", user_ids=" + user_ids +
                ", owner_id=" + owner_id +
                ", status=" + status +
                '}';
    }

    public Task() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public List<Integer> getUser_ids() {
        return user_ids;
    }

    public void setUser_ids(List<Integer> user_ids) {
        this.user_ids = user_ids;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

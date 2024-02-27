package com.example.project1.model;

import java.util.List;

public class Card {
        private int task_id,owner_id;
        private String title;
        private String description;
        private String start_date;
        private String end_date;
        private String priority;

        public Card(int task_id, int owner_id, String title, String description, String start_date, String end_date, String priority) {
                this.task_id = task_id;
                this.owner_id = owner_id;
                this.title = title;
                this.description = description;
                this.start_date = start_date;
                this.end_date = end_date;
                this.priority = priority;
//                this.assigned_users = assigned_users;
//                this.owner_firstname = owner_firstname;
//                this.owner_lastname = owner_lastname;
        }

        @Override
        public String toString() {
                return "Card{" +
                        "task_id=" + task_id +
                        ", ownerId=" + owner_id +
                        ", title='" + title + '\'' +
                        ", description='" + description + '\'' +
                        ", start_date='" + start_date + '\'' +
                        ", end_date='" + end_date + '\'' +
                        ", priority='" + priority + '\'' +
                        ", assigned_users=" + assigned_users +
                        ", owner_firstname='" + owner_firstname + '\'' +
                        ", owner_lastname='" + owner_lastname + '\'' +
                        '}';
        }

        public int getOwner_id() {
                return owner_id;
        }

        public void setOwner_id(int owner_id) {
                this.owner_id = owner_id;
        }

        private List<TempUser> assigned_users;
        private String owner_firstname,owner_lastname;

        public List<TempUser> getAssigned_users() {
                return assigned_users;
        }

        public void setAssigned_users(List<TempUser> assigned_users) {
                this.assigned_users = assigned_users;
        }

        public int getTask_id() {
                return task_id;
        }

        public void setTask_id(int task_id) {
                this.task_id = task_id;
        }

        public String getOwner_firstname() {
                return owner_firstname;
        }

        public void setOwner_firstname(String owner_firstname) {
                this.owner_firstname = owner_firstname;
        }

        public String getOwner_lastname() {
                return owner_lastname;
        }

        public void setOwner_lastname(String owner_lastname) {
                this.owner_lastname = owner_lastname;
        }





        public int getTaskId() {
                return task_id;
        }

        public void setTaskId(int taskId) {
                this.task_id = taskId;
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

        public String getPriority() {
                return priority;
        }

        public void setPriority(String priority) {
                this.priority = priority;
        }
// Add getters and setters as needed

}

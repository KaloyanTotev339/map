package com.example.listview;

public class Course {
    private String name;
    private String startTime;
    private String date;
    private String teacher;
    private String roomNumber;

    public Course(String courseName, String startTime, String teacher, String roomNumber) {
        this.name = courseName;
        this.startTime = startTime;
        this.teacher = teacher;
        this.roomNumber = roomNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        teacher = teacher;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Course(String name, String startTime, String date, String teacher, String roomNumber) {
        this.name = name;
        this.startTime = startTime;
        this.date = date;
        this.teacher = teacher;
        this.roomNumber = roomNumber;
    }
    @Override
    public String toString(){
        return String.format("[ %s, %s, %s, %s]",getTeacher(),getName(),getRoomNumber(),getDate());
    }
}

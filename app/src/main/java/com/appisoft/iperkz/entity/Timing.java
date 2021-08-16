package com.appisoft.iperkz.entity;


public class Timing {

    private String day;
    private String startTime;
    private String endTime;
    private Integer isToday;
    private Integer isOpen;
    private String endTimeObject;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getIsToday() {
        return isToday;
    }

    public void setIsToday(Integer isToday) {
        this.isToday = isToday;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public String getEndTimeObject() {
        return endTimeObject;
    }

    public void setEndTimeObject(String endTimeObject) {
        this.endTimeObject = endTimeObject;
    }
}

package com.appisoft.iperkz.entity;

public class SimpleResponse {

    private boolean result = false;
    private int messageCode = -1;
    private String message;
    public boolean isResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }
    public int getMessageCode() {
        return messageCode;
    }
    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

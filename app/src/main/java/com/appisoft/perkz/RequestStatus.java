package com.appisoft.perkz;

import java.util.HashMap;
import java.util.Map;

public class RequestStatus {
	private boolean result = false;
	private Map<String, Object> payload = new HashMap<String, Object>();
	private int messageCode =-1;

	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}

	public Map<String, Object> getPayload() {
		return payload;
	}
	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}
	public int getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(int messageCode) {
		this.messageCode = messageCode;
	}
	
	

}

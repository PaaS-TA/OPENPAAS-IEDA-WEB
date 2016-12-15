package org.openpaas.ieda.api.director.dto;

import java.util.List;

public class ResponseTaskOuput {
	public String state; //상태
	public String tag; //tag
	public List<String> messages; //message 
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	
}

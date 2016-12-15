package org.openpaas.ieda.api.task;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskOutputDTO {
	
	private double time; //시간
	private String stage; //단계
	private List<String> tags; //tags
	private String total; //total
	private String task; //task
	private String index; //index
	private String state; //상태
	private String progress; //진행
	private HashMap<String, String> data; //정보
	private HashMap<String, String> error; //오류
	
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public HashMap<String, String> getData() {
		return data;
	}
	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
	public HashMap<String, String> getError() {
		return error;
	}
	public void setError(HashMap<String, String> error) {
		this.error = error;
	}
	
	
}

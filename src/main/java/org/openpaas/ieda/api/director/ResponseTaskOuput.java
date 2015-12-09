package org.openpaas.ieda.api.director;

import java.util.List;

import lombok.Data;

@Data
public class ResponseTaskOuput {
	public String state;
	public List<String> messages; 
}

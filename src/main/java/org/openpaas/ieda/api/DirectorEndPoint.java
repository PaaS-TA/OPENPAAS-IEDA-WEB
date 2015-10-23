package org.openpaas.ieda.api;

import lombok.Data;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Data
public class DirectorEndPoint {
    private String host;
    private int    port;
    private String username;
    private String password;
    
    private static DirectorEndPoint director;
    
    public static DirectorEndPoint getInstance() {
    	if (director != null)
    		return director;
    	else
    		return new DirectorEndPoint();
    }
    
	public DirectorEndPoint() {	}

	public DirectorEndPoint(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
    
    
}

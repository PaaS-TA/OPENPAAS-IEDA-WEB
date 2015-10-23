package org.openpaas.ieda.web.config.setting;

public class DirectorException extends RuntimeException {
	
    public DirectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectorException(String message) {
        super(message);
    }

    public DirectorException(Throwable cause) {
        super(cause);
    }
}

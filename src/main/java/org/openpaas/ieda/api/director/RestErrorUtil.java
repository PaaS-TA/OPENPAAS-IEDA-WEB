package org.openpaas.ieda.api.director;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestErrorUtil {
    public static boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        
        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
                || HttpStatus.Series.SERVER_ERROR.equals(series));
    }
}

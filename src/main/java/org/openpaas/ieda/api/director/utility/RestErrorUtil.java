package org.openpaas.ieda.api.director.utility;

import org.springframework.http.HttpStatus;

public class RestErrorUtil {
	
	/***************************************************
	 * @project          : PaaS 플랫폼 설치 자동화
	 * @description   :  HTTP 상태 코드를 비교하여 여부 응답 
	 * @title               : isError
	 * @return            : boolean
	***************************************************/
	public static boolean isError(HttpStatus status) {
		
        HttpStatus.Series series = status.series();
        boolean flag = HttpStatus.Series.CLIENT_ERROR.equals(series)
                || HttpStatus.Series.SERVER_ERROR.equals(series);
        return flag;
    }
	
}


package com.manageleaguefootball.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_USER(9999, "UNCATEGORIZED", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_ALREADY_EXIST(1001, "USER_ALREADY_EXIST!", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1002, "This is Invalid Email!!!!", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "This is Invalid UserName!!!!", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "This is Invalid password!!!!", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1005, "INVALID_KEY, CHECK SOMEWHERE!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    
    UNAUTHENTICATED(1007, "UNAUTHENTICATED_ERROR", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "DO_NOT_HAVE_PERMISSION!", HttpStatus.FORBIDDEN),
    INVALID_DOB(1009, "Your age must be at least {min}!", HttpStatus.FORBIDDEN),
    MISSING_FILE(1010, "Missing field: ", HttpStatus.BAD_REQUEST),



    NOT_ENOUGH_TEAM(1011, "Not enough teams to generate schedule", HttpStatus.BAD_REQUEST),
    UPLOAD_IMAGE_FAILED(1012, "Image upload fail", HttpStatus.BAD_REQUEST),
    LEAGUE_NOT_FOUND(1013,"Can not find leaque",HttpStatus.NOT_FOUND),
    TEAM_NOT_FOUND(1014,"Can not find team",HttpStatus.NOT_FOUND),
    SEASON_NOT_FOUND(1014,"Can not find season",HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_FOUND(1015,"Can not find schedule",HttpStatus.NOT_FOUND),
    PLAYER_NOT_FOUND(1015,"Can not find player",HttpStatus.NOT_FOUND)

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}

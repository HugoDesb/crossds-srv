package io.github.hugodesb.matelist.controller.reponse.exceptions;

import java.util.Date;

public class ExceptionResponse {

    private Date timestamp;
    private String message;
    private String status;
    private ErrorName errorName;
    private String path;

    public ExceptionResponse(Date timestamp, String message, String status, ErrorName errorName) {
        this.timestamp = timestamp;
        this.message = message;
        this.status = status;
        this.errorName = errorName;
    }

    public ExceptionResponse(Date timestamp, String message, String description, String internalServerError) {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public ErrorName getErrorName() {
        return errorName;
    }
}

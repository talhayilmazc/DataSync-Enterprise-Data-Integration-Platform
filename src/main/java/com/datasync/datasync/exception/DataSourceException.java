package com.datasync.datasync.exception;

import org.springframework.http.HttpStatus;

public class DataSourceException extends BaseException {
    public DataSourceException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "DATASOURCE_ERROR");
    }
    public DataSourceException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
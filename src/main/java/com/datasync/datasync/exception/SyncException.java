package com.datasync.datasync.exception;

import org.springframework.http.HttpStatus;

public class SyncException extends BaseException {
    public SyncException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "SYNC_ERROR");
    }
    public SyncException(String message, String errorCode) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, errorCode);
    }
}
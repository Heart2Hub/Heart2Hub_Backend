package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateImageDocumentException extends RuntimeException{
    public UnableToCreateImageDocumentException() {
    }

    public UnableToCreateImageDocumentException(String msg) {
        super(msg);
    }
}

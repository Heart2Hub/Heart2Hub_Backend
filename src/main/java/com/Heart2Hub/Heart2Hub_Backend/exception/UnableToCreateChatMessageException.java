package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateChatMessageException extends RuntimeException {
    public UnableToCreateChatMessageException() {
    }

    public UnableToCreateChatMessageException(String msg) {
        super(msg);
    }
}

package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateConversationException extends RuntimeException{
    public UnableToCreateConversationException() {
    }

    public UnableToCreateConversationException(String message) {
        super(message);
    }
}

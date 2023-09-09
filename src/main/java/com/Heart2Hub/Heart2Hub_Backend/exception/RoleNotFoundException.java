package com.Heart2Hub.Heart2Hub_Backend.exception;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException() {
    }

    public RoleNotFoundException(String msg) {
        super(msg);
    }
}

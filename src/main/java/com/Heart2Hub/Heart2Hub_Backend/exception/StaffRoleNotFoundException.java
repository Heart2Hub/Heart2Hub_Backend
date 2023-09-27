package com.Heart2Hub.Heart2Hub_Backend.exception;

public class StaffRoleNotFoundException extends RuntimeException{
    public StaffRoleNotFoundException() {
    }

    public StaffRoleNotFoundException(String msg) {
        super(msg);
    }
}

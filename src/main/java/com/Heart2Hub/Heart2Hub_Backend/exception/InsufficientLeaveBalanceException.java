package com.Heart2Hub.Heart2Hub_Backend.exception;

public class InsufficientLeaveBalanceException extends RuntimeException{

    public InsufficientLeaveBalanceException() {}

    public InsufficientLeaveBalanceException(String msg) {super(msg);}
}

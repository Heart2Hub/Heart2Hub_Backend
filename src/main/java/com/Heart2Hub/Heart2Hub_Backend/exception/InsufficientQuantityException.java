package com.Heart2Hub.Heart2Hub_Backend.exception;

public class InsufficientQuantityException extends RuntimeException{

    public InsufficientQuantityException() {}

    public InsufficientQuantityException(String msg) {super(msg);}
}

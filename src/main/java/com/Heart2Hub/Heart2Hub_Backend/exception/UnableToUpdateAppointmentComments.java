package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToUpdateAppointmentComments extends RuntimeException{

    public UnableToUpdateAppointmentComments() {
    }

    public UnableToUpdateAppointmentComments(String msg) {
        super(msg);
    }

}

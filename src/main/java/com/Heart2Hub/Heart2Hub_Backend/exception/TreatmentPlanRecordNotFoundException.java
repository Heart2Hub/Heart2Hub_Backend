package com.Heart2Hub.Heart2Hub_Backend.exception;

public class TreatmentPlanRecordNotFoundException extends RuntimeException{
    public TreatmentPlanRecordNotFoundException() {
    }

    public TreatmentPlanRecordNotFoundException(String msg) {
        super(msg);
    }
}

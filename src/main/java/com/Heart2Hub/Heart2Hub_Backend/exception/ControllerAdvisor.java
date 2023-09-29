package com.Heart2Hub.Heart2Hub_Backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {


  // Handle all generic exceptions
  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleGenericException(final Exception ex) {
    return new ResponseEntity<>("Error Encountered: " + ex.getMessage(),
        HttpStatus.BAD_REQUEST);
  }

  // Handle user authentication error when user not found
  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleGenericException(BadCredentialsException ex) {
    return new ResponseEntity<>("Error Encountered: User Credentials Invalid",
        HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(StaffNotFoundException.class)
  public ResponseEntity<Object> handleStaffNotFoundException(
      StaffNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToCreateStaffException.class)
  public ResponseEntity<Object> handleUnableToCreateStaffException(
      UnableToCreateStaffException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToChangePasswordException.class)
  public ResponseEntity<Object> handleUnableToChangePasswordException(
      UnableToChangePasswordException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<Object> handleUsernameNotFoundException(
      UsernameNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(StaffDisabledException.class)
  public ResponseEntity<Object> handleStaffDisabledException(
      StaffDisabledException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(AppointmentAssignmentException.class)
  public ResponseEntity<Object> handleAppointmentAssignmentException(
      AppointmentAssignmentException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AppointmentNotFoundException.class)
  public ResponseEntity<Object> handleAppointmentNotFoundException(
      AppointmentNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToCreateAppointmentException.class)
  public ResponseEntity<Object> handleUnableToCreateAppointmentException(
      UnableToCreateAppointmentException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}

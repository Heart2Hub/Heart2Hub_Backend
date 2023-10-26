package com.Heart2Hub.Heart2Hub_Backend.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

  // Helper methods
  private Map<String, List<String>> getErrorsMap(List<String> errors) {
    Map<String, List<String>> errorResponse = new HashMap<>();
    errorResponse.put("errors", errors);
    return errorResponse;
  }

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

  @ExceptionHandler(UnableToUpdateAppointmentArrival.class)
  public ResponseEntity<Object> UnableToUpdateAppointmentArrivalException(
      UnableToUpdateAppointmentArrival ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToUpdateAppointmentComments.class)
  public ResponseEntity<Object> UnableToUpdateAppointmentCommentsException(
      UnableToUpdateAppointmentComments ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToCreateAppointmentException.class)
  public ResponseEntity<Object> handleUnableToCreateAppointmentException(
      UnableToCreateAppointmentException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToAssignAppointmentException.class)
  public ResponseEntity<Object> handleUnableToAssignAppointmentException(
      UnableToAssignAppointmentException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ElectronicHealthRecordNotFoundException.class)
  public ResponseEntity<Object> handleElectronicHealthRecordNotFoundException(
      ElectronicHealthRecordNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToAddImageAttachmentToAppointmentException.class)
  public ResponseEntity<Object> handleUnableToAddImageAttachmentToAppointmentException(
      UnableToAddImageAttachmentToAppointmentException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToUpdateTreatmentPlanRecordException.class)
  public ResponseEntity<Object> handleUnableToUpdateTreatmentPlanRecordException(
      UnableToUpdateTreatmentPlanRecordException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToUpdateInvitationException.class)
  public ResponseEntity<Object> handleUnableToUpdateInvitationException(
      UnableToUpdateInvitationException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    List<String> errorList = ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getDefaultMessage())
            .collect(Collectors.toList());
    ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errorList);
    return handleExceptionInternal(ex, errorDetails, headers, errorDetails.getStatus(), request);
//    return new ResponseEntity<>(getErrorsMap(errorList), new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }
}


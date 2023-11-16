package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  Optional<Appointment> findByAppointmentNehrId(UUID appointmentNehrId);
  List<Appointment> findAllByActualDateTimeBetween(LocalDateTime start, LocalDateTime end);
  List<Appointment> findAllByActualDateTimeBetweenAndDepartmentName(LocalDateTime start, LocalDateTime end, String departmentName);

  List<Appointment> findAllByBookedDateTimeBetween(LocalDateTime start, LocalDateTime end);
  List<Appointment> findAllByBookedDateTimeBetweenAndDepartmentName(LocalDateTime start, LocalDateTime end, String departmentName);
  List<Appointment> findAllByPatientUsername(String username);
  List<Appointment> findAllByActualDateTimeBetweenAndCurrentAssignedStaffUsername(LocalDateTime start, LocalDateTime end, String username);

  List<Appointment> findAllBySwimlaneStatusEnumEquals(SwimlaneStatusEnum swimlane);
}

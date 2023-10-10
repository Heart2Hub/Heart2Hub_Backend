package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.FacilityBooking;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.exception.OverlappingBookingException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToDeleteFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FacilityBookingService {
  private final FacilityBookingRepository facilityBookingRepository;
  private final FacilityRepository facilityRepository;
  private final SubDepartmentRepository subDepartmentRepository;
  private final StaffRepository staffRepository;

  public FacilityBookingService(FacilityBookingRepository facilityBookingRepository, FacilityRepository facilityRepository, SubDepartmentRepository subDepartmentRepository, StaffRepository staffRepository) {
    this.facilityBookingRepository = facilityBookingRepository;
    this.facilityRepository = facilityRepository;
    this.subDepartmentRepository = subDepartmentRepository;
    this.staffRepository = staffRepository;
  }

  // tbc - temporary functions to get my rostering to work

  public FacilityBooking createBooking(FacilityBooking booking, Long facilityId) {
    Facility facility = facilityRepository.findById(facilityId).get();
    facility.getListOfFacilityBookings().add(booking);
    booking.setFacility(facility);
    //facilityRepository.save(facility);
    return facilityBookingRepository.save(booking);
  }

  public void updateBooking(Long facilityBookingId, Long facilityId) {
    System.out.println("here1");
    FacilityBooking facilityBooking = facilityBookingRepository.findById(facilityBookingId).get();
    System.out.println("here2");
    Facility oldFacility = facilityBooking.getFacility();
    System.out.println("here3");
    oldFacility.getListOfFacilityBookings().remove(facilityBooking);
    System.out.println("here4");
    Facility facility = facilityRepository.findById(facilityId).get();
    System.out.println("here5");
    facility.getListOfFacilityBookings().add(facilityBooking);
    System.out.println("here6");
    facilityBooking.setFacility(facility);
  }

  public List<FacilityBooking> getAllFacilityBookings() {
    return facilityBookingRepository.findAll();
  }

  public List<FacilityBooking> getAllBookingsOfAFacility(Long id) {
    Facility f = facilityRepository.findById(id).get();
    return facilityBookingRepository.findAllByFacility(f);
  }

  public List<FacilityBooking> getAllBookingsOfAStaff(String username) {
    return facilityBookingRepository.findFacilityBookingByStaffUsername(username);
  }

  public FacilityBooking getFacilityBookingById(Long id) {
    return facilityBookingRepository.findById(id).get();
  }

  public FacilityBooking createFacilityBooking(FacilityBooking facilityBooking, Long facilityId) {
    // Check for overlapping bookings
    Facility facility = facilityRepository.findById(facilityId).get();
    List<FacilityBooking> overlappingBookings = facilityBookingRepository.findAllByFacility(facility);

    boolean hasOverlap = overlappingBookings.stream()
            .filter(booking -> !booking.equals(facilityBooking))
            .anyMatch(booking -> (facilityBooking.getStartDateTime().isBefore(booking.getEndDateTime()) || facilityBooking.getStartDateTime().isEqual(booking.getEndDateTime())) &&
                    (facilityBooking.getEndDateTime().isAfter(booking.getStartDateTime()) || facilityBooking.getEndDateTime().isEqual(booking.getStartDateTime())));

    if (hasOverlap) {
      throw new OverlappingBookingException("Booking overlaps with existing bookings");
    }

    if (facilityBooking.getStartDateTime().isAfter(facilityBooking.getEndDateTime())) {
      throw new OverlappingBookingException("Start Time is after End Time!");
    }

    if (facilityBooking.getStartDateTime().isEqual(facilityBooking.getEndDateTime())) {
      throw new OverlappingBookingException("Start Time same as End Time!");
    }
    facilityBooking.setFacility(facility);

    FacilityBooking fb = facilityBookingRepository.save(facilityBooking);

    Staff s = staffRepository.findByUsername(facilityBooking.getStaffUsername()).get();
    s.getListOfFacilityBookings().add(facilityBooking);
    staffRepository.save(s);

    facilityBooking.setFacility(facility);
    return fb;
  }

  public FacilityBooking updateFacilityBooking(LocalDateTime startDateTime, LocalDateTime endDateTime, String comments, Long bookingId) {
    FacilityBooking existingBooking = getFacilityBookingById(bookingId);

    List<FacilityBooking> overlappingBookings = facilityBookingRepository.findAllByFacility(existingBooking.getFacility());

    boolean hasOverlap = overlappingBookings.stream()
            .filter(booking -> !booking.equals(existingBooking))
            .anyMatch(booking -> (startDateTime.isBefore(booking.getEndDateTime())) &&
                    (endDateTime.isAfter(booking.getStartDateTime())));

    if (hasOverlap) {
      throw new OverlappingBookingException("Booking overlaps with existing bookings");
    }

    if (startDateTime.isAfter(endDateTime)) {
      throw new OverlappingBookingException("Start Time is after End Time!");
    }

    if (startDateTime.isEqual(endDateTime)) {
      throw new OverlappingBookingException("Start Time same as End Time!");
    }

    if (existingBooking.getShift() != null) {
      throw new UnableToDeleteFacilityException("Cannot change booking associated with a shift");
    }

    existingBooking.setComments(comments);
    existingBooking.setStartDateTime(startDateTime);
    existingBooking.setEndDateTime(endDateTime);
    facilityBookingRepository.save(existingBooking);

    return existingBooking;
  }

  public void deleteFacilityBooking(Long id) {
    FacilityBooking facilityBooking = getFacilityBookingById(id);

    Staff s = staffRepository.findByUsername(facilityBooking.getStaffUsername()).get();
if (facilityBooking.getShift() != null) {
  throw new UnableToDeleteFacilityException("Cannot delete booking associated with a shift");
}
    s.getListOfFacilityBookings().remove(facilityBooking);
    staffRepository.save(s);

    facilityBookingRepository.delete(facilityBooking);
  }
}

package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.FacilityBooking;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.FacilityBookingRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.FacilityRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.SubDepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FacilityBookingService {


  private final FacilityBookingRepository facilityBookingRepository;
  private final FacilityRepository facilityRepository;
  private final SubDepartmentRepository subDepartmentRepository;

  public FacilityBookingService(FacilityBookingRepository facilityBookingRepository, FacilityRepository facilityRepository, SubDepartmentRepository subDepartmentRepository) {
    this.facilityBookingRepository = facilityBookingRepository;
    this.facilityRepository = facilityRepository;
    this.subDepartmentRepository = subDepartmentRepository;
  }

  // tbc - temporary functions to get my rostering to work

  public FacilityBooking createBooking(FacilityBooking booking, Long facilityId) {
    Facility facility = facilityRepository.findById(facilityId).get();
    facility.getListOfFacilityBookings().add(booking);
    booking.setFacility(facility);
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


}

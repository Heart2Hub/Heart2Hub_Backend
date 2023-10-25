package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.SubDepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateWardException;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.WardClassRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.WardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WardService {

    private final WardRepository wardRepository;
    private final WardClassRepository wardClassRepository;
    private final StaffRepository staffRepository;

    public WardService(WardRepository wardRepository, WardClassRepository wardClassRepository, StaffRepository staffRepository) {
        this.wardRepository = wardRepository;
        this.wardClassRepository = wardClassRepository;
        this.staffRepository = staffRepository;
    }


    public boolean isLoggedInUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            Optional<Staff> currStaff = staffRepository.findByUsername(user.getUsername());
            if (currStaff.isPresent()) {
                StaffRoleEnum role = currStaff.get().getStaffRoleEnum();
                if (role == StaffRoleEnum.ADMIN) {
                    isAdmin = true;
                }
            }
        }
        return isAdmin;
    }

    public Ward createWard(Ward newWard, String wardClassName) throws UnableToCreateWardException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateWardException("Staff cannot create wards as he/she is not an admin.");
        }
        try {
            String name = newWard.getName();
            Integer capacity = newWard.getCapacity();
            String location = newWard.getLocation();
            if (name == null) {
                throw new UnableToCreateDepartmentException("Name must be present.");
            }
            if (capacity == null) {
                throw new UnableToCreateDepartmentException("Capacity must be present.");
            }
            if (location == null) {
                throw new UnableToCreateDepartmentException("Location must be present.");
            }
            List<WardClass> wardClassList = wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName);
            if (wardClassList.size() == 0) {
                throw new UnableToCreateDepartmentException("Ward class not found.");
            } else {
                WardClass wc = wardClassList.get(0);
                newWard.setWardClass(wc);

                LocalDateTime firstDate = LocalDateTime.now();
                int day = firstDate.getDayOfMonth();
                int month = firstDate.getMonthValue();
                int year = firstDate.getYear();
                int hour = firstDate.getHour();
                if (hour >= 12) {
                    day = day + 1;
                }
                firstDate = LocalDateTime.of(year, month, day, 12, 00, 00);

                for (int i = 0; i < 7; i++) {
                    WardAvailability wardAvailability = new WardAvailability(firstDate, newWard.getCapacity(), newWard);
                    newWard.getListOfWardAvailabilities().add(wardAvailability);
                    firstDate = firstDate.plusDays(1);
                }

                int bedsPerRoom = wc.getBedsPerRoom();
                int numOfRooms = capacity / bedsPerRoom;
                for (int i = 1; i <= numOfRooms; i++) {
                    for (int j = 1; j <= bedsPerRoom; j++) {
                        Admission admission = new Admission();
                        admission.setRoom(i);
                        admission.setBed(j);
                        newWard.getListOfCurrentDayAdmissions().add(admission);
                    }
                }

                wardRepository.save(newWard);

                return newWard;
            }
        } catch (Exception ex) {
            throw new UnableToCreateWardException(ex.getMessage());
        }
    }

    public List<Ward> getAllWardsByName(String name) throws DepartmentNotFoundException {
        try {
            List<Ward> wardList = wardRepository.findByNameContainingIgnoreCase(name);
            return wardList;
        } catch (Exception ex) {
            throw new SubDepartmentNotFoundException(ex.getMessage());
        }
    }

    public List<Ward> getAllWardsByWardClass(String wardClassName) {
        WardClass wardClass = wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName).get(0);
        return wardRepository.findByWardClass(wardClass);
    }

//    public Admission getCurrentDayAdmissionsForWard(String name) {
//        Ward ward = wardRepository.findByNameContainingIgnoreCase(name).get(0);
//        return ward.getListOfCurrentDayAdmissions()[0];
//    }


}

package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.entity.Ward;
import com.Heart2Hub.Heart2Hub_Backend.entity.WardClass;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateWardClassException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateWardException;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.WardClassRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.WardRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WardClassService {

    private final WardClassRepository wardClassRepository;
    private final StaffRepository staffRepository;

    public WardClassService(WardClassRepository wardClassRepository, StaffRepository staffRepository) {
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

    public WardClass createWardClass(WardClass wardClass) throws UnableToCreateWardClassException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateWardClassException("Staff cannot create wards as he/she is not an admin.");
        }
        try {
            String name = wardClass.getWardClassName();
            BigDecimal rate = wardClass.getWardClassRate();
            Integer beds = wardClass.getBedsPerRoom();
            if (name == null) {
                throw new UnableToCreateWardClassException("Name must be present");
            }
            if (rate == null) {
                throw new UnableToCreateWardClassException("Rate must be present");
            }
            if (beds == null) {
                throw new UnableToCreateWardClassException("Beds value must be present");
            }
            wardClassRepository.save(wardClass);
            return wardClass;
        } catch (Exception ex) {
            throw new UnableToCreateWardClassException(ex.getMessage());
        }
    }


}

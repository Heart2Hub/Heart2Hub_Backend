package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    List<Facility> findByFacilityStatusEnum(FacilityStatusEnum facilityStatusEnum);

    List<Facility> findByNameContainingIgnoreCase(String name);
}

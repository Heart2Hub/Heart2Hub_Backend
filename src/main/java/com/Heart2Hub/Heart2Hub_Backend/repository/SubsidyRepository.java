package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.ProblemRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.Subsidy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubsidyRepository extends JpaRepository<Subsidy, Long> {

    Optional<Subsidy> findBySubsidyNehrId(UUID subsidyNehrId);

}

package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Invitation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

  Optional<Invitation> findById(Long invitationId);
  Optional<Invitation> findByTreatmentPlanRecord_TreatmentPlanRecordIdAndStaff_StaffId(Long treatmentPlanRecordId, Long staffId);
  List<Invitation> findAllByTreatmentPlanRecord_TreatmentPlanRecordId(Long treatmentPlanRecordId);

  List<Invitation> findAllByStaffStaffId(Long staffId);
}

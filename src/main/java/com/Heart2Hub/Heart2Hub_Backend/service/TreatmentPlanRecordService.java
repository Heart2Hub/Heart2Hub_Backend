package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Invitation;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.entity.TreatmentPlanRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.TreatmentPlanRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToAddImageAttachmentToAppointmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateTreatmentPlanRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateNextOfKinRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToDeleteTreatmentPlanException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToUpdateTreatmentPlanRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.InvitationRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.TreatmentPlanRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.NextOfKinRecordRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TreatmentPlanRecordService {

  private final TreatmentPlanRecordRepository treatmentPlanRecordRepository;

  private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

  private final InvitationRepository invitationRepository;

  private final StaffService staffService;

  public TreatmentPlanRecordService(TreatmentPlanRecordRepository treatmentPlanRecordRepository,
      ElectronicHealthRecordRepository electronicHealthRecordRepository,
      InvitationRepository invitationRepository,
      StaffService staffService) {
    this.treatmentPlanRecordRepository = treatmentPlanRecordRepository;
    this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    this.invitationRepository = invitationRepository;
    this.staffService = staffService;
  }

  public TreatmentPlanRecord createTreatmentPlanRecord(Long electronicHealthRecordId, Long staffId,
      TreatmentPlanRecord newTreatmentPlanRecord)
      throws UnableToCreateTreatmentPlanRecordException {
    try {
      ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordRepository.findById(
          electronicHealthRecordId).get();
      electronicHealthRecord.getListOfTreatmentPlanRecords().add(newTreatmentPlanRecord);
      treatmentPlanRecordRepository.save(newTreatmentPlanRecord);
      electronicHealthRecordRepository.save(electronicHealthRecord);

      //create invitation for primary staff
      Staff staff = staffService.findById(staffId);
      Invitation newInvitation = new Invitation("self", true);
      newInvitation.setTreatmentPlanRecord(newTreatmentPlanRecord);
      newInvitation.setStaff(staff);
      invitationRepository.save(newInvitation);
      staff.getListOfInvitations().add(newInvitation);

      return newTreatmentPlanRecord;
    } catch (Exception ex) {
      throw new UnableToCreateTreatmentPlanRecordException(ex.getMessage());
    }
  }

  public TreatmentPlanRecord updateTreatmentPlanRecord(Long electronicHealthRecordId,
      Long treatmentPlanRecordId, Long staffId, TreatmentPlanRecord treatmentPlanRecord)
      throws UnableToUpdateTreatmentPlanRecordException {
    try {
      Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
          electronicHealthRecordId);

      if (electronicHealthRecordOptional.isEmpty()) {
        throw new UnableToUpdateTreatmentPlanRecordException("No such EHR is found");
      } else {
        ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordOptional.get();
        TreatmentPlanRecord currentTreatmentPlanRecord = getTreatmentPlanById(
            treatmentPlanRecordId);

        if (!electronicHealthRecord.getListOfTreatmentPlanRecords()
            .contains(currentTreatmentPlanRecord)) {
          throw new UnableToUpdateTreatmentPlanRecordException(
              "EHR Does not contain this Treatment Plan");
        }

        System.out.println("CHECKING IF STAFF HAS ACCESS");
        System.out.println(checkStaffHasInvitationToTreatmentPlanRecord(treatmentPlanRecordId,staffId));

        if (!checkStaffHasInvitationToTreatmentPlanRecord(treatmentPlanRecordId,staffId)) {
          throw new UnableToUpdateTreatmentPlanRecordException(
              "Unable to update without access");
        }

        currentTreatmentPlanRecord.setTreatmentPlanTypeEnum(
            treatmentPlanRecord.getTreatmentPlanTypeEnum());
        currentTreatmentPlanRecord.setDescription(treatmentPlanRecord.getDescription());
        if (treatmentPlanRecord.getStartDate().isBefore(treatmentPlanRecord.getEndDate())) {
          currentTreatmentPlanRecord.setStartDate(treatmentPlanRecord.getStartDate());
          currentTreatmentPlanRecord.setEndDate(treatmentPlanRecord.getEndDate());
        } else {
          throw new UnableToUpdateTreatmentPlanRecordException("Cannot set End Date before Start Date");
        }
        return currentTreatmentPlanRecord;
      }
    } catch (Exception ex) {
      throw new UnableToUpdateTreatmentPlanRecordException(ex.getMessage());
    }
  }

  public TreatmentPlanRecord completeTreatmentPlanRecord (Long electronicHealthRecordId,
      Long treatmentPlanRecordId, Long staffId)
      throws UnableToUpdateTreatmentPlanRecordException {
    try {
      Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
          electronicHealthRecordId);

      if (electronicHealthRecordOptional.isEmpty()) {
        throw new UnableToUpdateTreatmentPlanRecordException("No such EHR is found");
      } else {
        ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordOptional.get();
        TreatmentPlanRecord currentTreatmentPlanRecord = getTreatmentPlanById(
            treatmentPlanRecordId);

        if (!electronicHealthRecord.getListOfTreatmentPlanRecords()
            .contains(currentTreatmentPlanRecord)) {
          throw new UnableToUpdateTreatmentPlanRecordException(
              "EHR Does not contain this Treatment Plan");
        }

        System.out.println("CHECKING IF STAFF HAS ACCESS");
        System.out.println(checkStaffHasInvitationToTreatmentPlanRecord(treatmentPlanRecordId,staffId));

        if (!checkStaffHasInvitationToTreatmentPlanRecord(treatmentPlanRecordId,staffId)) {
          throw new UnableToUpdateTreatmentPlanRecordException(
              "Unable to update without access");
        }

        if (currentTreatmentPlanRecord.getIsCompleted()) {
          throw new UnableToUpdateTreatmentPlanRecordException("Treatment Plan is already Completed!");
        }

        currentTreatmentPlanRecord.setIsCompleted(true);
        currentTreatmentPlanRecord.setEndDate(LocalDateTime.now());
        return currentTreatmentPlanRecord;
      }
    } catch (Exception ex) {
      throw new UnableToUpdateTreatmentPlanRecordException(ex.getMessage());
    }
  }

  public Boolean deleteTreatmentPlanRecord(Long electronicHealthRecordId,
      Long treatmentPlanRecordId, Long staffId) {

    Optional<TreatmentPlanRecord> treatmentPlanRecordOptional = treatmentPlanRecordRepository.findById(
        treatmentPlanRecordId);

    Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
        electronicHealthRecordId);

    if (treatmentPlanRecordOptional.isEmpty()) {
      throw new UnableToUpdateTreatmentPlanRecordException("No such Treatment Plan is found");
    }
    if (electronicHealthRecordOptional.isEmpty()) {
      throw new UnableToUpdateTreatmentPlanRecordException("No such EHR is found");
    }

    TreatmentPlanRecord currentTreatmentPlanRecord = treatmentPlanRecordOptional.get();
    ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordOptional.get();

    //check if caller created the treatment plan, only primary staff can delete
    Optional<Invitation> invitationOptional = invitationRepository.findByTreatmentPlanRecord_TreatmentPlanRecordIdAndStaff_StaffId(
        treatmentPlanRecordId, staffId);
    if (invitationOptional.isEmpty() || !invitationOptional.get().getIsPrimary()) {
      throw new UnableToUpdateTreatmentPlanRecordException(
          "Unable to delete as you are not the primary staff (creator) of the plan");
    }

    //check if there are outstanding invitations, if have need delete them and delink from the staff as well
    List<Invitation> listOfInvitations = getListOfInvitationsInTreatmentPlanRecord(
        currentTreatmentPlanRecord.getTreatmentPlanRecordId());
    if (!listOfInvitations.isEmpty()) {
      for (Invitation invitation : listOfInvitations) {
        Staff invitedStaff = invitation.getStaff();
        invitedStaff.getListOfInvitations().remove(invitation);
        invitationRepository.delete(invitation);
      }
    }

    //now remove treatment plan from EHR
    if (electronicHealthRecord.getListOfTreatmentPlanRecords()
        .contains(currentTreatmentPlanRecord)) {
      electronicHealthRecord.getListOfTreatmentPlanRecords().remove(currentTreatmentPlanRecord);
    } else {
      throw new UnableToDeleteTreatmentPlanException("EHR does not have given Treatment Plan");
    }

    //now can delete
    treatmentPlanRecordRepository.delete(currentTreatmentPlanRecord);
    return true;
  }

  //view image
  public List<ImageDocument> viewTreatmentPlanRecordImages(Long treatmentPlanRecordId) {
    TreatmentPlanRecord treatmentPlanRecord = getTreatmentPlanById(treatmentPlanRecordId);
    return treatmentPlanRecord.getListOfImageDocuments();
  }

  //add image
  public TreatmentPlanRecord addImageAttachmentToTreatmentPlan(Long treatmentPlanRecordId,
      String imageLink,
      String createdDate, Long staffId) {
    try {
      System.out.println("Uploading image");
      System.out.println("treatment " + treatmentPlanRecordId);
      System.out.println("staff " + staffId);


      System.out.println("CHECKING IF STAFF HAS ACCESS");
      System.out.println(checkStaffHasInvitationToTreatmentPlanRecord(treatmentPlanRecordId,staffId));

      if (!checkStaffHasInvitationToTreatmentPlanRecord(treatmentPlanRecordId,staffId)) {
        throw new UnableToUpdateTreatmentPlanRecordException(
            "Cannot add images to a treatment plan that you have no access to");
      }

      TreatmentPlanRecord treatmentPlanRecord = getTreatmentPlanById(treatmentPlanRecordId);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
      LocalDateTime createdDateTime = LocalDateTime.parse(createdDate, formatter);
      ImageDocument imageDocument = new ImageDocument(imageLink, createdDateTime);
      treatmentPlanRecord.getListOfImageDocuments().add(imageDocument);

      return treatmentPlanRecord;
    } catch (Exception ex) {
      throw new UnableToUpdateTreatmentPlanRecordException(ex.getMessage());
    }
  }

  //remove image
  public TreatmentPlanRecord removeImageAttachmentFromTreatmentPlan(Long treatmentPlanRecordId,
      String imageLink,
      Long staffId) {

    if (!checkStaffHasInvitationToTreatmentPlanRecord(treatmentPlanRecordId,staffId)) {
      throw new UnableToUpdateTreatmentPlanRecordException(
          "Cannot remove images to a treatment plan that you have no access to");
    }

    TreatmentPlanRecord treatmentPlanRecord = getTreatmentPlanById(treatmentPlanRecordId);

    List<ImageDocument> imageDocumentToRemove = treatmentPlanRecord.getListOfImageDocuments()
        .stream().filter(imageDocument -> imageDocument.getImageLink().equals(imageLink)).toList();
    if (imageDocumentToRemove.isEmpty()) {
      throw new UnableToUpdateTreatmentPlanRecordException("No image exist");
    }
    ImageDocument imageDocument = imageDocumentToRemove.get(0);
    treatmentPlanRecord.getListOfImageDocuments().remove(imageDocument);

    //not deleting imageDocument
//    ImageDocumentService imageDocument

    return treatmentPlanRecord;
  }

  //add invitation
  public TreatmentPlanRecord addInvitationToTreatmentPlanRecord(Long treatmentPlanRecordId,  Long staffId, Long invitedStaffId) {
    if (!checkStaffIsPrimary(treatmentPlanRecordId,staffId)) {
      throw new UnableToUpdateTreatmentPlanRecordException("Cannot invite other staff if you are not the primary of this treatment plan");
    }
    try {
      System.out.println("CALLING ADD INVITATION");
      TreatmentPlanRecord treatmentPlanRecord = getTreatmentPlanById(treatmentPlanRecordId);
      Staff invitedStaff = staffService.getStaffById(invitedStaffId);
      Staff staff = staffService.getStaffById(staffId);
      String signature = staff.getFirstname() + " " + staff.getLastname() + " (" + staff.getStaffRoleEnum() + ")";

      Invitation invitation = new Invitation(signature, false );
      invitation.setStaff(invitedStaff);
      invitation.setTreatmentPlanRecord(treatmentPlanRecord);
      invitationRepository.save(invitation);
      System.out.println(invitation.getInvitedBy());
      System.out.println(invitation.getCreatedDate());
      System.out.println(invitation.getIsPrimary());
      System.out.println(invitation.getInvitationId());
      System.out.println(invitation.getStaff().getUsername());
      System.out.println(invitation.getTreatmentPlanRecord().getTreatmentPlanRecordId());
      invitedStaff.getListOfInvitations().add(invitation);
      return treatmentPlanRecord;
    } catch (Exception ex) {

      System.out.println("ERROR GETTING THROWN IN SERVICE");
      System.out.println(ex.getMessage());
      throw new UnableToUpdateTreatmentPlanRecordException(ex.getMessage());
    }
  }

  //delete invitation
  public TreatmentPlanRecord deleteInvitationToTreatmentPlanRecord(Long treatmentPlanRecordId,  Long staffId, Long invitationId) {
    if (!checkStaffIsPrimary(treatmentPlanRecordId,staffId)) {
      throw new UnableToUpdateTreatmentPlanRecordException("Cannot delete invitation to treatment plan if you are not the primary of this treatment plan");
    }

    Optional<Invitation> invitationOptional = invitationRepository.findById(invitationId);
    if (invitationOptional.isEmpty()) {
      throw new UnableToUpdateTreatmentPlanRecordException("Invitation does not exist");
    }
    Invitation invitation = invitationOptional.get();

    //check if caller is primary
    if (!checkStaffIsPrimary(treatmentPlanRecordId,staffId)){
      throw new UnableToUpdateTreatmentPlanRecordException("You cannot delete invitation as you are not the primary of this treatment plan record");
    };

    //Cannot remove self, unless is delete treatment plan use case
    if (invitation.getIsPrimary()) {
      throw new UnableToUpdateTreatmentPlanRecordException("Cannot delete own invitation");
    }

    Staff invitedStaffToRemove = invitation.getStaff();
    invitedStaffToRemove.getListOfInvitations().remove(invitation);
    invitationRepository.delete(invitation);

    return getTreatmentPlanById(treatmentPlanRecordId);
  }

  //get by id
  public TreatmentPlanRecord getTreatmentPlanById(Long treatmentPlanRecordId) {
    Optional<TreatmentPlanRecord> treatmentPlanRecordOptional = treatmentPlanRecordRepository.findById(
        treatmentPlanRecordId);
    if (treatmentPlanRecordOptional.isPresent()) {
      return treatmentPlanRecordOptional.get();
    } else {
      throw new TreatmentPlanRecordNotFoundException("Treatment Plan Record Id does not exist");
    }
  }

  //get list of invitations to view
  public List<Invitation> getListOfInvitationsInTreatmentPlanRecord(Long treatmentPlanRecordId) {
    return invitationRepository.findAllByTreatmentPlanRecord_TreatmentPlanRecordId(
        treatmentPlanRecordId);
  }

  public Boolean checkStaffHasInvitationToTreatmentPlanRecord(Long treatmentPlanRecordId, Long staffId) {
    return invitationRepository.findByTreatmentPlanRecord_TreatmentPlanRecordIdAndStaff_StaffId(
        treatmentPlanRecordId, staffId).isPresent();
  }

  public Boolean checkStaffIsPrimary(Long treatmentPlanRecordId, Long staffId) {
     Optional<Invitation> invitationOptional = invitationRepository.findByTreatmentPlanRecord_TreatmentPlanRecordIdAndStaff_StaffId(
        treatmentPlanRecordId, staffId);
     if (invitationOptional.isEmpty()) {
       throw new UnableToUpdateTreatmentPlanRecordException("Staff has no access to this treatment plan record");
     }

     Invitation invitation = invitationOptional.get();
     return invitation.getIsPrimary();
  }
}

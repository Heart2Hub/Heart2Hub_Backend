package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.InvitationDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Invitation;
import com.Heart2Hub.Heart2Hub_Backend.service.ElectronicHealthRecordService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InvitationMapper {

  private final ElectronicHealthRecordService electronicHealthRecordService;

  public InvitationMapper(ElectronicHealthRecordService electronicHealthRecordService) {
    this.electronicHealthRecordService = electronicHealthRecordService;
  }

  public InvitationDTO toDTO(Invitation invitation) {

    if (invitation == null) {
      return null;
    }

    InvitationDTO dto = new InvitationDTO();
    dto.setInvitationId(invitation.getInvitationId());
    dto.setCreatedDate(invitation.getCreatedDate());
    dto.setInvitedBy(invitation.getInvitedBy());
    dto.setIsPrimary(invitation.getIsPrimary());
    dto.setIsRead(invitation.getIsRead());
    dto.setIsApproved(invitation.getIsApproved());
    dto.setTreatmentPlanRecordId(invitation.getTreatmentPlanRecord().getTreatmentPlanRecordId());
    dto.setStaffId(invitation.getStaff().getStaffId());
    dto.setUsername(invitation.getStaff().getUsername());
    dto.setFirstname(invitation.getStaff().getFirstname());
    dto.setLastname(invitation.getStaff().getLastname());
    dto.setMobileNumber(invitation.getStaff().getMobileNumber());
    dto.setIsHead(invitation.getStaff().getIsHead());
    dto.setStaffRoleEnum(invitation.getStaff().getStaffRoleEnum());

    Long ehrId = electronicHealthRecordService.findEHRByTreatmentPlanId(
        invitation.getTreatmentPlanRecord().getTreatmentPlanRecordId())
            .getElectronicHealthRecordId();

    dto.setElectronicHealthRecordId(ehrId);
    return dto;
  }

  //DO NOT USE
  public Invitation toEntity(InvitationDTO dto) {
    if (dto == null) {
      return null;
    }

    Invitation invitation = new Invitation();
    invitation.setInvitationId(dto.getInvitationId());
    invitation.setIsPrimary(dto.getIsPrimary());
    invitation.setCreatedDate(dto.getCreatedDate());
    invitation.setInvitedBy(dto.getInvitedBy());

    return invitation;

  }

  public List<InvitationDTO> toDTOList(List<Invitation> invitations) {
    List<InvitationDTO> dtos = new ArrayList<>();
    for (Invitation invitation : invitations) {
      dtos.add(toDTO(invitation));
    }
    return dtos;
  }

  //DO NOT USE
  public List<Invitation> toEntityList(List<InvitationDTO> dtos) {
    List<Invitation> invitations = new ArrayList<>();
    for (InvitationDTO dto : dtos) {
      invitations.add(toEntity(dto));
    }
    return invitations;
  }
}

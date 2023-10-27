package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.AdmissionDTO;
import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdmissionMapper {

    public AdmissionDTO toDTO(Admission admission) {
        if (admission == null) {
            return null;
        }

        AdmissionDTO dto = new AdmissionDTO();

        dto.setAdmissionId(admission.getAdmissionId());
        dto.setReason(admission.getReason());
        dto.setComments(admission.getComments());
        dto.setDuration(admission.getDuration());
        dto.setRoom(admission.getRoom());
        dto.setBed(admission.getBed());
        dto.setArrived(admission.getArrived());
        dto.setAdmissionDateTime(admission.getAdmissionDateTime());
        dto.setDischargeDateTime(admission.getDischargeDateTime());

        //Assigned doctor
        if (admission.getCurrentAssignedDoctor() != null) {
            dto.setAssignedDoctorId(admission.getCurrentAssignedDoctor().getStaffId());
        }

        if (admission.getCurrentAssignedNurse() != null) {
            dto.setAssignedNurseId(admission.getCurrentAssignedNurse().getStaffId());
        }

        if (admission.getCurrentAssignedAdmin() != null) {
            dto.setAssignedAdminId(admission.getCurrentAssignedAdmin().getStaffId());
        }


        //Patient
        if (admission.getPatient() != null) {
            dto.setPatientId(admission.getPatient().getPatientId());
            dto.setUsername(admission.getPatient().getUsername());

            if (admission.getPatient().getProfilePicture() != null) {
                dto.setPatientProfilePicture(admission.getPatient().getProfilePicture().getImageLink());
            }

            if (admission.getPatient().getElectronicHealthRecord() != null) {
                dto.setElectronicHealthRecordId(admission.getPatient().getElectronicHealthRecord().getElectronicHealthRecordId());
                dto.setFirstName(admission.getPatient().getElectronicHealthRecord().getFirstName());
                dto.setLastName(admission.getPatient().getElectronicHealthRecord().getLastName());
                dto.setNric(admission.getPatient().getElectronicHealthRecord().getNric());
                dto.setPlaceOfBirth(admission.getPatient().getElectronicHealthRecord().getPlaceOfBirth());
                dto.setSex(admission.getPatient().getElectronicHealthRecord().getSex());
                dto.setContactNumber(admission.getPatient().getElectronicHealthRecord().getContactNumber());
                dto.setNationality(admission.getPatient().getElectronicHealthRecord().getNationality());
                dto.setDateOfBirth(admission.getPatient().getElectronicHealthRecord().getDateOfBirth());
            }

        }

        // Convert listOfImageDocuments to listOfImageDocumentsImageLinks
//        if (appointment.getListOfImageDocuments() != null) {
//            List<String> imageLinks = new ArrayList<>();
//            for (ImageDocument imageDoc : appointment.getListOfImageDocuments()) {
//                imageLinks.add(imageDoc.getImageLink());
//            }
//            dto.setListOfImageDocumentsImageLinks(imageLinks);
//        }



        // Add other complex mappings if needed...

        return dto;
    }
}
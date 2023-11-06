package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.AdmissionDTO;
import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        //Ward
        if (admission.getWard() != null) {
            dto.setWard(admission.getWard().getName());
        }

        //Assigned staff
        List<Staff> listOfAssignedStaff = admission.getListOfAssignedStaff();
        List<Long> listOfStaffIds = listOfAssignedStaff
                .stream()
                .map(Staff::getStaffId) // Assuming you have a getStaffId method in Staff
                .collect(Collectors.toList());
        dto.setListOfStaffsId(listOfStaffIds);

        //Medication order
        List<MedicationOrder> listOfMedicationOrders = admission.getListOfMedicationOrders();
        List<Long> listOfMedicationOrderIds = listOfMedicationOrders
                .stream()
                .map(MedicationOrder::getMedicationOrderId) // Assuming you have a getStaffId method in Staff
                .collect(Collectors.toList());
        dto.setListOfMedicationOrderIds(listOfMedicationOrderIds);

//        for (Staff staff : listOfAssignedStaff) {
//            String fullname = staff.getFirstname() + " " + staff.getLastname();
//            if (staff.getStaffRoleEnum().equals(StaffRoleEnum.ADMIN)) {
//                dto.setAssignedAdminId(staff.getStaffId());
//                dto.setAssignedAdminName(fullname);
//            } else if (staff.getStaffRoleEnum().equals(StaffRoleEnum.NURSE)) {
//                dto.setAssignedNurseId(staff.getStaffId());
//                dto.setAssignedNurseName(fullname);
//            } else if (staff.getStaffRoleEnum().equals(StaffRoleEnum.DOCTOR)) {
//                dto.setAssignedDoctorId(staff.getStaffId());
//                dto.setAssignedDoctorName(fullname);
//            }
//        }


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
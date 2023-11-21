package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.ElectronicHealthRecordDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicalHistoryRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.ProblemRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.Subsidy;
import com.Heart2Hub.Heart2Hub_Backend.entity.TreatmentPlanRecord;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ElectronicHealthRecordMapper {

  private final ModelMapper modelMapper;

  public ElectronicHealthRecordMapper() {
    this.modelMapper = new ModelMapper();

    modelMapper.createTypeMap(ElectronicHealthRecord.class, ElectronicHealthRecordDTO.class)
//        .addMapping(src -> src.getContactNumber() != null ? src.getContactNumber() : null,
//            ElectronicHealthRecordDTO::setContactNumber)
        .addMapping(src -> (src.getListOfMedicalHistoryRecords() != null) ?
                src.getListOfMedicalHistoryRecords().stream().map(MedicalHistoryRecord::getMedicalHistoryRecordId).collect(Collectors.toList())
                : null,
            ElectronicHealthRecordDTO::setListOfMedicalHistoryRecordsId)
        .addMapping(src -> (src.getListOfProblemRecords() != null) ?
                src.getListOfProblemRecords().stream().map(ProblemRecord::getProblemRecordId).collect(Collectors.toList())
                : null,
            ElectronicHealthRecordDTO::setListOfProblemRecordsId)
        .addMapping(src -> (src.getListOfPrescriptionRecords() != null) ?
                src.getListOfPrescriptionRecords().stream().map(PrescriptionRecord::getPrescriptionRecordId).collect(Collectors.toList())
                : null,
            ElectronicHealthRecordDTO::setListOfPrescriptionRecordsId)
        .addMapping(src -> (src.getListOfTreatmentPlanRecords() != null) ?
                src.getListOfTreatmentPlanRecords().stream().map(TreatmentPlanRecord::getTreatmentPlanRecordId).collect(Collectors.toList())
                : null,
            ElectronicHealthRecordDTO::setListOfTreatmentPlanRecordsId)
        .addMapping(src -> (src.getListOfPastAppointments() != null) ?
                src.getListOfPastAppointments().stream().map(Appointment::getAppointmentId).collect(Collectors.toList())
                : null,
            ElectronicHealthRecordDTO::setListOfPastAppointmentsId)
        .addMapping(src -> (src.getListOfSubsidies() != null) ?
                src.getListOfSubsidies().stream().map(Subsidy::getSubsidyId).collect(Collectors.toList())
                : null,
            ElectronicHealthRecordDTO::setListOfSubsidiesId)
        .addMapping(src -> (src.getListOfPastAdmissions() != null) ?
                src.getListOfPastAdmissions().stream().map(Admission::getAdmissionId).collect(Collectors.toList())
                : null,
            ElectronicHealthRecordDTO::setListOfPastAdmissionsId)
        .addMapping(src -> (src.getListOfNextOfKinRecords() != null) ?
                src.getListOfNextOfKinRecords().stream().map(NextOfKinRecord::getNextOfKinRecordId).collect(Collectors.toList())
                : null,
            ElectronicHealthRecordDTO::setListOfNextOfKinRecordsId);
  }

  public ElectronicHealthRecordDTO convertToDto(ElectronicHealthRecord electronicHealthRecord) {
    return modelMapper.map(electronicHealthRecord, ElectronicHealthRecordDTO.class);
  }

  public ElectronicHealthRecord convertToEntity(ElectronicHealthRecordDTO electronicHealthRecordDTO) {
    return modelMapper.map(electronicHealthRecordDTO, ElectronicHealthRecord.class);
  }
}

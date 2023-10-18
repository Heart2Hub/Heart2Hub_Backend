package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.NehrDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NehrMapper {

  private final ModelMapper modelMapper;

  public NehrMapper() {
    this.modelMapper = new ModelMapper();

    modelMapper.createTypeMap(ElectronicHealthRecord.class, NehrDTO.class)
            .addMapping(src -> (src.getListOfSubsidies() != null) ?
                            src.getListOfSubsidies().stream().map(Subsidy::getSubsidyId).collect(Collectors.toList())
                            : null,
                    NehrDTO::setListOfSubsidies)
            .addMapping(src -> (src.getListOfPastAdmissions() != null) ?
                            src.getListOfPastAdmissions().stream().map(Admission::getAdmissionId).collect(Collectors.toList())
                            : null,
                    NehrDTO::setListOfPastAdmissions)
            .addMapping(src -> (src.getListOfPastAppointments() != null) ?
                            src.getListOfPastAppointments().stream().map(Appointment::getAppointmentId).collect(Collectors.toList())
                            : null,
                    NehrDTO::setListOfPastAppointments)
            .addMapping(src -> (src.getListOfNextOfKinRecords() != null) ?
                            src.getListOfNextOfKinRecords().stream().map(NextOfKinRecord::getNextOfKinRecordId).collect(Collectors.toList())
                            : null,
                    NehrDTO::setListOfNextOfKinRecords)
            .addMapping(src -> (src.getListOfPrescriptionRecords() != null) ?
                            src.getListOfPrescriptionRecords().stream().map(PrescriptionRecord::getPrescriptionRecordId).collect(Collectors.toList())
                            : null,
                    NehrDTO::setListOfPrescriptionRecords)
            .addMapping(src -> (src.getListOfProblemRecords() != null) ?
                            src.getListOfProblemRecords().stream().map(ProblemRecord::getProblemRecordId).collect(Collectors.toList())
                            : null,
                    NehrDTO::setListOfProblemRecords)
            .addMapping(src -> (src.getListOfMedicalHistoryRecords() != null) ?
                            src.getListOfMedicalHistoryRecords().stream().map(MedicalHistoryRecord::getMedicalRecordId).collect(Collectors.toList())
                            : null,
                    NehrDTO::setListOfMedicalHistoryRecords)
            .addMapping(src -> (src.getListOfTreatmentPlanRecords() != null) ?
                            src.getListOfTreatmentPlanRecords().stream().map(TreatmentPlanRecord::getTreatmentPlanRecordId).collect(Collectors.toList())
                            : null,
                    NehrDTO::setListOfTreatmentPlanRecords);
  }

  public NehrDTO convertToDto(ElectronicHealthRecord electronicHealthRecord) {
    return modelMapper.map(electronicHealthRecord, NehrDTO.class);
  }

  public ElectronicHealthRecord convertToEntity(NehrDTO nehrDTO) {
    return modelMapper.map(nehrDTO, ElectronicHealthRecord.class);
  }
}

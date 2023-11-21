package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Patient;
import com.Heart2Hub.Heart2Hub_Backend.entity.PatientRequest;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PatientRequestEnum;
import com.Heart2Hub.Heart2Hub_Backend.repository.AdmissionRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRequestRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatientRequestService {

    private final PatientRequestRepository patientRequestRepository;

    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;

    public PatientRequestService(PatientRequestRepository patientRequestRepository, PatientRepository patientRepository, AdmissionRepository admissionRepository) {
        this.patientRequestRepository = patientRequestRepository;
        this.patientRepository = patientRepository;
        this.admissionRepository = admissionRepository;
    }

    public List<PatientRequest> getPatientRequests(String username) {
        Patient patient = patientRepository.findByUsername(username).get();

        return patient.getAdmission().getListOfPatientRequests();
    }

    public PatientRequest createPatientRequest(String requestName, String username) {
        Patient patient = patientRepository.findByUsername(username).get();

        PatientRequestEnum patientRequestEnum = PatientRequestEnum.valueOf(requestName);
        PatientRequest patientRequest = new PatientRequest(patientRequestEnum);

        patient.getAdmission().getListOfPatientRequests().add(patientRequest);
        patientRequestRepository.save(patientRequest);

        return patientRequest;
    }

    public String deletePatientRequest(String requestName, String username) {
        Patient patient = patientRepository.findByUsername(username).get();
        List<PatientRequest> currentPatientRequests = patient.getAdmission().getListOfPatientRequests();
        PatientRequest patientRequestToDelete = new PatientRequest();

        for (PatientRequest patientRequest : currentPatientRequests) {
            if (patientRequest.getPatientRequestEnum() == PatientRequestEnum.valueOf(requestName)) {
                patientRequestToDelete = patientRequest;
            }
        }

        patient.getAdmission().getListOfPatientRequests().remove(patientRequestToDelete);
        patientRequestRepository.delete(patientRequestToDelete);

        return "Patient Request Deleted";

    }
}

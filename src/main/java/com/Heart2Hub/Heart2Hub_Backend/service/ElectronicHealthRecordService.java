package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.configuration.Heart2HubConfig;
import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ElectronicHealthRecordService {

  private final PasswordEncoder passwordEncoder;

  private final Heart2HubConfig heart2HubConfig;

  private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

  public ElectronicHealthRecordService(PasswordEncoder passwordEncoder,
      Heart2HubConfig heart2HubConfig,
      ElectronicHealthRecordRepository electronicHealthRecordRepository) {
    this.passwordEncoder = passwordEncoder;
    this.heart2HubConfig = heart2HubConfig;
    this.electronicHealthRecordRepository = electronicHealthRecordRepository;
  }

  public ElectronicHealthRecord getElectronicHealthRecordByIdAndDateOfBirth(
      Long electronicHealthRecordId, LocalDateTime dateOfBirth)
      throws ElectronicHealthRecordNotFoundException {
    try {
      Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
          electronicHealthRecordId);

      if (electronicHealthRecordOptional.isPresent()) {
        ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordOptional.get();
        if (!dateOfBirth.equals(electronicHealthRecord.getDateOfBirth())) {
          throw new ElectronicHealthRecordNotFoundException("Date of Birth is invalid");
        } else {
          return electronicHealthRecord;
        }
      } else {
        throw new ElectronicHealthRecordNotFoundException(
            "Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
      }
    } catch (Exception ex) {
      throw new ElectronicHealthRecordNotFoundException(ex.getMessage());
    }
  }

  public ElectronicHealthRecord getElectronicHealthRecordById(
      Long electronicHealthRecordId) {
    Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
        electronicHealthRecordId);
    if (electronicHealthRecordOptional.isPresent()) {
      return electronicHealthRecordOptional.get();
    } else {
      throw new ElectronicHealthRecordNotFoundException(
          "Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
    }
  }

  public List<ElectronicHealthRecord> getAllElectronicHealthRecords() {
    return electronicHealthRecordRepository.findAll();
  }

  public ElectronicHealthRecord getElectronicHealthRecordByUsername(String username)
      throws ElectronicHealthRecordNotFoundException {
    return electronicHealthRecordRepository.findByPatientUsername(username).orElseThrow(
        () -> new ElectronicHealthRecordNotFoundException(
            "Electronic Health Record does not exist for " + username));
  }

  public ElectronicHealthRecord getNehrRecordByNric(String nric) {
    try {
      final String uri = "http://localhost:3002/records/" + nric;
      HttpHeaders headers = new HttpHeaders();
      headers.set("encoded-message", encodeSecretMessage());
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
      RestTemplate restTemplate = new RestTemplate();
      ElectronicHealthRecord result = restTemplate.exchange(uri, HttpMethod.GET, entity,
          ElectronicHealthRecord.class).getBody();
      return result;
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      return null;
    }
  }

  public String encodeSecretMessage() {
    try {
      String SECRET_MESSAGE = heart2HubConfig.getJwt().getSecretMessage();
      String SECRET_KEY = heart2HubConfig.getJwt().getSecretKey();

      Mac hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8),
          "HmacSHA256");
      hmac.init(secretKeySpec);

      byte[] hash = hmac.doFinal(SECRET_MESSAGE.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);

    } catch (Exception e) {
      throw new RuntimeException("Failed to compute HMAC", e);
    }
  }

  public ElectronicHealthRecord updateElectronicHealthRecord(Long electronicHealthRecordId,
      ElectronicHealthRecord newElectronicHealthRecord)
      throws ElectronicHealthRecordNotFoundException {
    try {
      Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
          electronicHealthRecordId);

      if (electronicHealthRecordOptional.isPresent()) {
        ElectronicHealthRecord existingElectronicHealthRecord = electronicHealthRecordOptional.get();
        existingElectronicHealthRecord.setFirstName(newElectronicHealthRecord.getFirstName());
        existingElectronicHealthRecord.setLastName(newElectronicHealthRecord.getLastName());
        existingElectronicHealthRecord.setSex(newElectronicHealthRecord.getSex());
        existingElectronicHealthRecord.setDateOfBirth(newElectronicHealthRecord.getDateOfBirth());
        existingElectronicHealthRecord.setPlaceOfBirth(newElectronicHealthRecord.getPlaceOfBirth());
        existingElectronicHealthRecord.setNationality(newElectronicHealthRecord.getNationality());
        existingElectronicHealthRecord.setRace(newElectronicHealthRecord.getRace());
        existingElectronicHealthRecord.setAddress(newElectronicHealthRecord.getAddress());
        existingElectronicHealthRecord.setContactNumber(
            newElectronicHealthRecord.getContactNumber());

        electronicHealthRecordRepository.save(existingElectronicHealthRecord);
        return existingElectronicHealthRecord;
      } else {
        throw new ElectronicHealthRecordNotFoundException(
            "Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
      }
    } catch (Exception ex) {
      throw new ElectronicHealthRecordNotFoundException(ex.getMessage());
    }
  }

  public ElectronicHealthRecord findByNric(String nric) {
    try {
      return electronicHealthRecordRepository.findByNricIgnoreCase(nric).get();
    } catch (Exception ex) {
      throw new ElectronicHealthRecordNotFoundException("Invalid NRIC");
    }
  }

  public ElectronicHealthRecord findEHRByTreatmentPlanId(Long treatmentPlanId) {
    return electronicHealthRecordRepository.findByListOfTreatmentPlanRecords_TreatmentPlanRecordId(
        treatmentPlanId).orElseThrow(
        () -> new ElectronicHealthRecordNotFoundException("No such EHR found for treatment plan"));
  }
}

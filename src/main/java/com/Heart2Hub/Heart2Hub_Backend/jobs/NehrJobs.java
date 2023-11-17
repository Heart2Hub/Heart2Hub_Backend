package com.Heart2Hub.Heart2Hub_Backend.jobs;

import com.Heart2Hub.Heart2Hub_Backend.dto.NehrDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreatePatientException;
import com.Heart2Hub.Heart2Hub_Backend.mapper.NehrMapper;
import com.Heart2Hub.Heart2Hub_Backend.service.ElectronicHealthRecordService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class NehrJobs {

    static final Logger LOGGER = Logger.getLogger(NehrJobs.class.getName());

    private final ElectronicHealthRecordService electronicHealthRecordService;

    public NehrJobs(ElectronicHealthRecordService electronicHealthRecordService) {
        this.electronicHealthRecordService = electronicHealthRecordService;
    }

//    @Scheduled(cron = "0 0 2 * * *") // Push every 2am for deployment
//    @Scheduled(cron = "0 * * * * *") // Push every minute for debugging
//    @SchedulerLock(name = "pushToNehr")
//    public void pushToNehr() {
//        LOGGER.info("Pushing to NEHR at "+ LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
//        try {
//            List<ElectronicHealthRecord> records = electronicHealthRecordService.getAllElectronicHealthRecords();
//            for (ElectronicHealthRecord record : records) {
//                RestTemplate restTemplate = new RestTemplate();
//                String nricToUpdate = record.getNric();
//                String endpointUrl = "http://localhost:3002/records/" + nricToUpdate;
//                HttpHeaders headers = new HttpHeaders();
//                headers.set("encoded-message", electronicHealthRecordService.encodeSecretMessage());
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                NehrMapper nehrMapper = new NehrMapper();
//                NehrDTO payload = nehrMapper.convertToDto(record);
//                HttpEntity<NehrDTO> requestEntity = new HttpEntity<>(payload, headers);
//                ResponseEntity<NehrDTO> responseEntity = restTemplate.exchange(endpointUrl, HttpMethod.PUT, requestEntity, NehrDTO.class);
//                if (!responseEntity.getStatusCode().is2xxSuccessful()) {
//                    NehrDTO nehrResponse = responseEntity.getBody();
//                    LOGGER.info("Problem pushing to NEHR server: " + nehrResponse);
//                }
//            }
//            LOGGER.info("Updated records to NEHR server.");
//        } catch (Exception ex) {
//            LOGGER.info("Problem pushing to NEHR server: " + ex.getMessage());
//        }
//    }

//     This runs very often. May want to think of a way to improve efficiency. E.g. setting flags if updated.
//     @Scheduled(cron = "0 0/30 * * * *") // Pull every half hour for deployment
//    @Scheduled(cron = "0 * * * * *") // Pull every minute for debugging
//    @SchedulerLock(name = "pullFromNehr")
//    public void pullFromNehr() {
//        try {
//            LOGGER.info("Pulling NEHR records at" + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
//            RestTemplate restTemplate = new RestTemplate();
//            String endpointUrl = "http://localhost:3002/records";
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("encoded-message", electronicHealthRecordService.encodeSecretMessage());
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//            ResponseEntity<NehrDTO[]> responseEntity = restTemplate.exchange(
//                    endpointUrl, HttpMethod.GET, requestEntity, NehrDTO[].class
//            );
//            NehrDTO[] nehrDTOS = responseEntity.getBody();
//            if (nehrDTOS != null) {
//                for (NehrDTO nehrDTO : nehrDTOS) {
//                    try {
//                        LOGGER.info("Updating record:" + nehrDTO);
//                        NehrMapper nehrMapper = new NehrMapper();
//                        ElectronicHealthRecord record = nehrMapper.convertToEntity(nehrDTO);
//                        electronicHealthRecordService.updateCascadeElectronicHealthRecord(electronicHealthRecordService.findByNric(record.getNric()).getElectronicHealthRecordId(), record);
//                    } catch (ElectronicHealthRecordNotFoundException ex) {
//                        LOGGER.info("EHR record not in Heart2Hub. Skipping...");
//                    } catch (Exception ex) {
//                        LOGGER.info("Problem updating EHR record from NEHR server. Skipping...");
//                    }
//                }
//                LOGGER.info("Updated records from NEHR server.");
//            } else {
//                LOGGER.info("Records are null from NEHR server.");
//            }
//        } catch (Exception ex) {
//            LOGGER.info("Problem pulling from NEHR server: " + ex.getMessage());
//        }
//    }

}

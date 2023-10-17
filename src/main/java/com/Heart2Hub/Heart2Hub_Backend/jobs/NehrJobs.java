package com.Heart2Hub.Heart2Hub_Backend.jobs;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
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

    // @Scheduled(cron = "0 0 2 * * *") // Pull every 2am for deployment
    // TO-DO: Update with NEHR DTO
    @Scheduled(cron = "0/30 * * * * *") // Pull every minute for debugging
    @SchedulerLock(name = "pushToNehr")
    public void pushToNehr() {
        LOGGER.info("Pushing to NEHR at "+ LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        List<ElectronicHealthRecord> records = electronicHealthRecordService.getAllElectronicHealthRecords();
        for (ElectronicHealthRecord record : records) {

            RestTemplate restTemplate = new RestTemplate();
            String nricToUpdate = record.getNric();
            String nehrUpdateUrl = "http://localhost:3002/records/" + nricToUpdate;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ElectronicHealthRecord> requestEntity = new HttpEntity<>(record, headers);
            restTemplate.exchange(nehrUpdateUrl, HttpMethod.PUT, requestEntity, ElectronicHealthRecord.class);
        }
        LOGGER.info("Updated records to NEHR server.");
    }

    // This runs very often. May want to think of a way to improve efficiency. E.g. setting flags if updated.
    // TO-DO: Update with NEHR DTO
    @Scheduled(cron = "0 0/30 * * * *") // Pull every half hour for deployment
    // @Scheduled(cron = "0 * * * * *") // Pull every minute for debugging
    @SchedulerLock(name = "pullFromNehr")
    public void pullFromNehr() {
        LOGGER.info("Pulling NEHR records at" + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        RestTemplate restTemplate = new RestTemplate();
        ElectronicHealthRecord[] records = restTemplate.getForObject("http://localhost:3002/records", ElectronicHealthRecord[].class);
        if (records != null) {
            for (ElectronicHealthRecord record : records) {
                try {
                    electronicHealthRecordService.updateElectronicHealthRecord(electronicHealthRecordService.findByNric(record.getNric()).getElectronicHealthRecordId(), record);
                } catch (ElectronicHealthRecordNotFoundException ex) {
                    LOGGER.info("EHR record not in Heart2Hub. Skipping...");
                } catch (Exception ex) {
                    LOGGER.info("Problem updating EHR record from NEHR server. Skipping...");
                }
            }
            LOGGER.info("Updated records from NEHR server.");
        } else {
            LOGGER.info("Problem pulling from NEHR server.");
        }
    }

}

package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.repository.LeaveBalanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaveBalanceService {
    @Autowired
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveBalanceService(LeaveBalanceRepository leaveBalanceRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    public Optional<LeaveBalance> getLeaveBalanceById(Long id) {
        return leaveBalanceRepository.findById(id);
    }

    public List<LeaveBalance> getAllLeaveBalance() { return  leaveBalanceRepository.findAll();}

    public void updateLeaveBalance (LeaveBalance leaveBalance) {
        Optional<LeaveBalance> optionalLeaveBalance = leaveBalanceRepository.findById(leaveBalance.getLeaveBalanceId());

        if (optionalLeaveBalance.isPresent()) {
            leaveBalanceRepository.save(leaveBalance);

        }
    }


}

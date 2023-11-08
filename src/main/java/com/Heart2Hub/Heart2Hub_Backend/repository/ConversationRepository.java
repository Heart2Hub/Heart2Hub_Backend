package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Conversation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

  List<Conversation> findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(Long staffId, Long staffId2);

  List<Conversation> findAllByFirstStaff_StaffId(Long staffId);

  List<Conversation> findAllBySecondStaff_StaffId(Long staffId);
}

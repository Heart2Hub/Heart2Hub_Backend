package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "conversation")
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long conversationId;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
//  @JoinColumn(name = "conversation_id", nullable = true)
  private Patient patient;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
//  @JoinColumn(name = "conversation_id", nullable = true)
  private Staff firstStaff;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
//  @JoinColumn(name = "conversation_id", nullable = true)
  private Staff secondStaff;

  @OneToMany(fetch = FetchType.LAZY)
  private List<ChatMessage> listOfChatMessages;

  public Conversation() {
    this.listOfChatMessages = new ArrayList<>();
  }

  public Conversation(Patient patient, Staff firstStaff, Staff secondStaff) {
    this();
    this.patient = patient;
    this.firstStaff = firstStaff;
    this.secondStaff = secondStaff;
  }
}

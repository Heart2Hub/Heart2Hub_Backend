package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.MessageTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "chatMessage")
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long chatMessageId;

  @NotBlank(message = "Content cannot be empty")
  @NotNull(message = "Content cannot be null")
  @Size(max = 1000)
  private String content;
  @Enumerated(EnumType.STRING)
  @NotNull
  private MessageTypeEnum messageTypeEnum;

  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timestamp;

  @NotNull
  private Long senderId;


  public ChatMessage() {
    this.timestamp = LocalDateTime.now();
  }

  public ChatMessage(String content, MessageTypeEnum messageTypeEnum, Long senderId){
    this();
    this.content = content;
    this.messageTypeEnum = messageTypeEnum;
    this.senderId = senderId;
  }
}

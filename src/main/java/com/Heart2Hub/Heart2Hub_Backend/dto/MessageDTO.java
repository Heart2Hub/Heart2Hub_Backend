package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageDTO {

//  private String senderName;
//  private String receiverName;
//  private String message;
//  private String date;
//  private MessageTypeEnum messageTypeEnum;

  private Long senderStaffId;
  private Long receiverStaffId;
  private Long timestamp;
  private String status;
  private String message;
}

package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class StaffChatDTO {

  //Staff
  private Long staffId;
  private String username;
  private String firstname;
  private String lastname;
  private Long mobileNumber;
  private Boolean isHead = false;
  private StaffRoleEnum staffRoleEnum;
  private String imageLink;

  //dept
  private String departmentName;
}


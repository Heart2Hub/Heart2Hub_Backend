package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Table(name = "staff")
public class Staff implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long staffId;
  @NotNull
  @Size(min = 6)
  @Column(unique = true)
  private String username;
  @NotNull
  @Column(unique = true)
  private String password;
  @NotNull
  private String firstname;
  @NotNull
  private String lastname;
  @NotNull
  @DecimalMin("79999999")
  @DecimalMax("100000000")
  private Long mobileNumber;

  @NotNull
  private Boolean isHead = false;

  @NotNull
  @Enumerated(EnumType.STRING)
  private StaffRoleEnum staffRoleEnum;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
  private List<Leave> listOfLeaves;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
  private List<Leave> listOfManagedLeaves;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
  private List<Shift> listOfShifts;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "currentAssignedStaff")
  private List<Appointment> listOfAssignedAppointments;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @NotNull
  @JoinColumn(name = "leave_balance_id")
  private LeaveBalance leaveBalance = new LeaveBalance();

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private ShiftPreference shiftPreference;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Invitation> listOfInvitations;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Post> listOfPosts;

  @OneToOne(cascade = CascadeType.ALL, optional = true)
  private ImageDocument profilePicture;

  @ManyToOne
  @JoinColumn(name = "sub_department_id")
  private SubDepartment subDepartment;

  @NotNull
  private Boolean disabled = false;

  public Staff() {
    this.listOfLeaves = new ArrayList<>();
    this.listOfManagedLeaves = new ArrayList<>();
    this.listOfShifts = new ArrayList<>();
    this.listOfAssignedAppointments = new ArrayList<>();
    this.listOfInvitations = new ArrayList<>();
    this.listOfPosts = new ArrayList<>();
  }

  public Staff(String username, String password, String firstname, String lastname,
      Long mobileNumber, StaffRoleEnum staffRoleEnum, Boolean isHead) {
    this();
    this.username = username;
    this.password = password;
    this.firstname = firstname;
    this.lastname = lastname;
    this.mobileNumber = mobileNumber;
      this.staffRoleEnum = staffRoleEnum;
      this.isHead = isHead;
      this.leaveBalance = new LeaveBalance();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(staffRoleEnum.toString()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}

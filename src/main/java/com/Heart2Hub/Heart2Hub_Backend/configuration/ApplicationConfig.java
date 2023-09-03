package com.Heart2Hub.Heart2Hub_Backend.configuration;

import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final StaffRepository staffRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return (username) -> {
      Optional<Staff> staffOptional = staffRepository.findByUsername(username);

      //User used here is part of the UserDetails
      //TODO to add Patient here next
      if (staffOptional.isPresent()) {
        return User.withUsername(username).password(staffOptional.get().getPassword())
            .roles(staffOptional.get().getRoleEnum().toString())
            .build();
      } else {
        throw new UsernameNotFoundException("User not found");
      }
    };
  }

  //act as the DAO for fetching user details, encode passwords and more
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());

    //need to know what algo used to encode the password to decode
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}


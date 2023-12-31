package com.Heart2Hub.Heart2Hub_Backend.configuration;

import com.Heart2Hub.Heart2Hub_Backend.filter.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
//
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //1. whitelist any of the request to pass without checking,
    //2. except for the rest behind
    //3. session management specifies your session creation policy, which we want to be stateless
    //4. we want to execute this JWTAuthFilter before the username password filter

    http.csrf(AbstractHttpConfigurer::disable)     //Disable CSRF to prevent POST methods from having issues
        .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(     //header frame
            FrameOptionsConfig::sameOrigin))
//        .cors(AbstractHttpConfigurer::disable)
        .cors(httpSecurityCorsConfigurer -> {     //Handling CORS issue here
          List<String> list = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
          CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
          corsConfiguration.setAllowedMethods(list);
          corsConfiguration.addAllowedOrigin("http://localhost:3000");
          corsConfiguration.addAllowedOrigin("ws://localhost:3000");
          corsConfiguration.addAllowedOriginPattern("http://localhost:3000");
          corsConfiguration.addAllowedOriginPattern("ws://localhost:3000");
          corsConfiguration.setMaxAge(3600l);
          corsConfiguration.setAllowCredentials(false);
          httpSecurityCorsConfigurer.configurationSource(request -> corsConfiguration);
        })

        //TODO CHANGE THE ROLES
        .authorizeHttpRequests((authorizeHttpRequests) -> // 1
            authorizeHttpRequests
                .requestMatchers("/staff/staffLogin").permitAll()
                .requestMatchers("/staff/getStaffByUsername").permitAll()
                .requestMatchers("/staff/changePassword").permitAll()
                .requestMatchers("/staff/**").permitAll()
                    .requestMatchers("/patient/**").permitAll()
                    .requestMatchers("/electronicHealthRecord/**").permitAll() // temp fixes for mobile to work
                    .requestMatchers("/nextOfKinRecord/**").permitAll()
                    .requestMatchers("/appointment/**").permitAll()
                    .requestMatchers("/shift/**").permitAll()
                    .requestMatchers("/department/**").permitAll()
                .requestMatchers("/nextOfKinRecord/**").permitAll()
                .requestMatchers("/treatmentPlanRecord/**").permitAll()
                .requestMatchers("/ws/info/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .anyRequest().authenticated() // 2
        )
        .sessionManagement((httpSecuritySessionManagementConfigurer -> //3
            httpSecuritySessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //4

    return http.build();
  }


}


//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//  private final JwtAuthenticationFilter jwtAuthFilter;
//  private final AuthenticationProvider authenticationProvider;
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http.csrf(AbstractHttpConfigurer::disable)
//        .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(
//            FrameOptionsConfig::sameOrigin))
//        .authorizeHttpRequests((authorizeHttpRequests) ->
//            authorizeHttpRequests
//                .requestMatchers("/staff/staffLogin").permitAll()
//                .requestMatchers("/staff/getStaffByUsername").permitAll()
//                .requestMatchers("/staff/changePassword").permitAll()
//                .requestMatchers("/staff/**").permitAll()
//                .requestMatchers("/patient/**").permitAll()
//                .requestMatchers("/electronicHealthRecord/**").permitAll()
//                .requestMatchers("/nextOfKinRecord/**").permitAll()
//                .requestMatchers("/appointment/**").permitAll()
//                .requestMatchers("/shift/**").permitAll()
//                .requestMatchers("/department/**").permitAll()
//                .requestMatchers("/nextOfKinRecord/**").permitAll()
//                .requestMatchers("/treatmentPlanRecord/**").permitAll()
//                .anyRequest().authenticated()
//        )
//        .sessionManagement((httpSecuritySessionManagementConfigurer ->
//            httpSecuritySessionManagementConfigurer
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)))
//        .authenticationProvider(authenticationProvider)
//        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//    return http.build();
//  }
//}


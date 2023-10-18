package com.Heart2Hub.Heart2Hub_Backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "heart2hub")
public class Heart2HubConfig {

  private Jwt jwt;

  public Jwt getJwt() {
    return jwt;
  }

  public void setJwt(Jwt jwt) {
    this.jwt = jwt;
  }

  public static class Jwt {

    private Long accessTokenExpiry;
    private Long refreshTokenExpiry;
    private String secretKey;
    private String secretMessage;

    public String getSecretKey() {
      return secretKey;
    }

    public void setSecretKey(String secretKey) {
      this.secretKey = secretKey;
    }

    public Long getAccessTokenExpiry() {
      return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(Long accessTokenExpiry) {
      this.accessTokenExpiry = accessTokenExpiry;
    }

    public Long getRefreshTokenExpiry() {
      return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(Long refreshTokenExpiry) {
      this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String getSecretMessage() {
      return secretMessage;
    }

    public void setSecretMessage(String secretMessage) {
      this.secretMessage = secretMessage;
    }

  }
}

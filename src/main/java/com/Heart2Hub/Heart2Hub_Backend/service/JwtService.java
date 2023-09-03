package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.configuration.Heart2HubConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Transactional
public class JwtService {



  private final Heart2HubConfig heart2HubConfig;

  public JwtService(Heart2HubConfig heart2HubConfig) {
    this.heart2HubConfig = heart2HubConfig;
  }

//  private static final String SECRET_KEY = heart2HubConfig.getJwt().getSecretKey();

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  //generate token without extra claims
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

    extraClaims.put("roles", userDetails.getAuthorities());
    List<String> roles = new ArrayList<>();
    for (GrantedAuthority authority : userDetails.getAuthorities()) {
      roles.add(authority.getAuthority());
    }

    return Jwts.builder()
        .claim("roles", roles)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + heart2HubConfig.getJwt()
            .getAccessTokenExpiry()))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // validate if token is valid to userdetails
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }


  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(heart2HubConfig.getJwt().getSecretKey());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  //for getting custom claims
  public String extractClaim(String token, String claimName) {
    final Claims claims = extractAllClaims(token);
    return claims.get(claimName, String.class);
  }

}


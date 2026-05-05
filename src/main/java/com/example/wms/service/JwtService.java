package com.example.wms.service;

import com.example.wms.entity.Users;
import com.example.wms.enumz.UserRoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretkey;

    public String generateToken(Users users) {

        String roleName = UserRoleType.fromRoleId(String.valueOf(users.getRoleid()))
                .map(Enum::name)
                .orElse("UNKNOWN");

        return Jwts.builder()
                .setSubject(users.getUsername())
                .claim("userid",users.getUserid())
                .claim("role",roleName)
                .claim("type","access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1))
                .signWith(Keys.hmacShaKeyFor(secretkey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Users users) {
        String roleName = UserRoleType.fromRoleId(String.valueOf(users.getRoleid()))
                .map(Enum::name)
                .orElse("UNKNOWN");

        return Jwts.builder()
                .setSubject(users.getUsername())
                .claim("userid",users.getUserid())
                .claim("role",roleName)
                .claim("type","refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 1))
                .signWith(Keys.hmacShaKeyFor(secretkey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretkey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(secretkey.getBytes())
                .build()
                .parseClaimsJws(token); // otomatis throw exception kalau invalid
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractUserid(String token) {
        return (String) extractAllClaims(token).get("userid");
    }

    public String extractRoleName(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    public String extractTokenType(String token) {
        return (String) extractAllClaims(token).get("type");
    }

    public Date extractExpiredAt(String token){
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        }catch (ExpiredJwtException e){
            return false;
        }catch (JwtException e ){
            return false;
        }catch (Exception e){
            return false;
        }
    }
}

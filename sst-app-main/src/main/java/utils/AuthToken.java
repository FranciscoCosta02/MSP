package utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static java.time.Instant.now;

public class AuthToken {
    public static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24; //1 day
    public String username;
    public String role;
    public String tokenID;
    public String jwtToken;

    public AuthToken() {
    }

    public AuthToken(String username, String role) {
        this.username = username;
        this.role = role;
        this.tokenID = UUID.randomUUID().toString();
        String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf407";


        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        this.jwtToken = Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .setSubject("Token")
                .setId(tokenID)
                .setIssuedAt(Date.from(now()))
                .setExpiration(Date.from(now().plusMillis(EXPIRATION_TIME)))
                .signWith(hmacKey)
                .compact();


    }

    public static long getExpirationTime() {
        return EXPIRATION_TIME;
    }
}
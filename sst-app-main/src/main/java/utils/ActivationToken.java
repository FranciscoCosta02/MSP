package utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static java.time.Instant.now;

public class ActivationToken {
    public static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24; //1 day
    public String email;
    public String tokenID;
    public String jwtToken;

    public ActivationToken() {
    }

    public ActivationToken(String email) {
        this.email = email;
        this.tokenID = UUID.randomUUID().toString();
        String secret = "asdfSFS34wf4249GsdGSFH4fsdfSDgrd5634gsfOWEK5354fdGdf4Y09";


        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        this.jwtToken = Jwts.builder()
                .claim("email", email)
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
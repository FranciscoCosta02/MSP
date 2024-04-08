package utils;

import com.google.cloud.datastore.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class JWTActivationValidation {

    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("ActivationToken");
    private static String secret = "asdfSFS34wf4249GsdGSFH4fsdfSDgrd5634gsfOWEK5354fdGdf4Y09";
    public JWTActivationValidation() {}

    public static Jws<Claims> parseJwt(String jwtString) {
        java.security.Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());
        Transaction txn = datastore.newTransaction();
        try {
            Jws<Claims> jwt = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(jwtString);
            Key tokenKey = tokenKeyFactory.newKey(jwt.getBody().getId());
            Entity token = txn.get(tokenKey);
            if(token == null){
                deleteToken(txn, jwtString);
                txn.commit();
                return null;
            }
            txn.commit();
            return jwt;
        } catch (Exception e) {
            deleteToken(txn, jwtString);
            txn.commit();
            return null;
        }
    }

    private static void deleteToken(Transaction txn, String jwtString) {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind("Token")
                .setFilter(StructuredQuery.PropertyFilter.eq("jwtToken", jwtString)).build();
        QueryResults<Entity> results = txn.run(query);
        do{
            Entity next = results.next();
            Key id = next.getKey();
            txn.delete(id);
        } while (results.hasNext());
    }
}

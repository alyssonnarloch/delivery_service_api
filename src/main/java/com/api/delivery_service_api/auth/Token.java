package com.api.delivery_service_api.auth;

import java.util.Arrays;
import java.util.List;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;

public class Token {

    public Token() {
    }

    public String generate() {

        try {
            // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
            RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);

            // Give the JWK a Key ID (kid), which is just the polite thing to do
            rsaJsonWebKey.setKeyId("k1");

            // Create the Claims, which will be the content of the JWT
            JwtClaims claims = new JwtClaims();
            claims.setIssuer("Issuer");  // who creates the token and signs it
            //claims.setAudience("Audience"); // to whom the token is intended to be sent
            //claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
           // claims.setGeneratedJwtId(); // a unique identifier for the token
           // claims.setIssuedAtToNow();  // when the token was issued/created (now)
            //claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
            //claims.setSubject("subject"); // the subject/principal is whom the token is about
            claims.setClaim("email", "mail@example.com"); // additional claims/attributes about the subject can be added            
            List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
            claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array

            // A JWT is a JWS and/or a JWE with JSON claims as the payload.
            // In this example it is a JWS so we create a JsonWebSignature object.
            JsonWebSignature jws = new JsonWebSignature();

            // The payload of the JWS is JSON content of the JWT Claims
            jws.setPayload(claims.toJson());

            // The JWT is signed using the private key
            jws.setKey(rsaJsonWebKey.getPrivateKey());
            System.out.println("Private Key: " + rsaJsonWebKey.getKeyId());

            // Set the Key ID (kid) header because it's just the polite thing to do.
            // We only have one key in this example but a using a Key ID helps
            // facilitate a smooth key rollover process
            jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

            // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

            // Sign the JWS and produce the compact serialization or the complete JWT/JWS
            // representation, which is a string consisting of three dot ('.') separated
            // base64url-encoded parts in the form Header.Payload.Signature
            // If you wanted to encrypt it, you can simply set this jwt as the payload
            // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
            String jwt = jws.getCompactSerialization();

            // Now you can do something with the JWT. Like send it to some other party
            // over the clouds and through the interwebs.
            System.out.println("JWT: " + jwt);
            
            return jwt;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return "";
    }

}

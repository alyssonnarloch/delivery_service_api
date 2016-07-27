package com.api.delivery_service_api.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;

public class Token {

    public final static String SECRET_KEY_AUTH = "aBH0gO6Ph3lGs4qF0ba5pen55W1f1W9i";
    public final static String SECRET_KEY_REQ = "4BJQ61oVd9uw75V4fNXnYLBnxI6mx789";
    public final static long TIMEOUT_MS_AUTH = 20000;
    public final static long TIMEOUT_MS_REQ = 360000;

    private String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    public Object getClaim(String claim, String signature) {
        return Jwts.parser().setSigningKey(signature).parseClaimsJws(this.token).getBody().get(claim);
    }

    public String generateForAuthentication(String email, String password) {
        SignatureAlgorithm hs512 = SignatureAlgorithm.HS512;

        long currentTime = System.currentTimeMillis();

        String token = Jwts.builder()
                .claim("email", email)
                .claim("password", password)
                .claim("expireTime", currentTime + TIMEOUT_MS_AUTH)
                .signWith(hs512, SECRET_KEY_AUTH)
                .compact();

        return token;
    }

    public String generateForRequest(int userId) {
        SignatureAlgorithm hs512 = SignatureAlgorithm.HS512;

        long currentTime = System.currentTimeMillis();

        String token = Jwts.builder()
                .claim("id", userId)
                .claim("expireTime", currentTime + TIMEOUT_MS_REQ)
                .signWith(hs512, SECRET_KEY_REQ)
                .compact();

        byte[] encodedToken = Base64.encodeBase64(token.getBytes());

        return new String(encodedToken);
    }

}

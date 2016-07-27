package com.api.delivery_service_api.auth;

import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;

@Provider
public class FilterAuthentication implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext containerRequest) throws IOException {

        String path = containerRequest.getUriInfo().getPath();

        if (path != null && !path.equals("auth/login") && !path.equals("test/token")) {
            try {
                String fullHeader = containerRequest.getHeaderString("Authorization");
                String[] headerParts = fullHeader.split(" ");
                String type = headerParts[0];
                String authToken = headerParts[1];
                String authTokenDecoded = new String(Base64.decodeBase64(authToken.getBytes()));

                Token token = new Token(authTokenDecoded);

                long currentTimeMS = System.currentTimeMillis();
                long expireTime = (long) token.getClaim("expireTime", Token.SECRET_KEY_REQ);

                if (currentTimeMS > expireTime) {
                    throw new WebApplicationException(Status.UNAUTHORIZED);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
        }
    }

}

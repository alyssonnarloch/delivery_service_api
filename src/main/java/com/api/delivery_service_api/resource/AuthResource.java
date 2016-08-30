package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.auth.Token;
import com.api.delivery_service_api.model.Auth;
import com.api.delivery_service_api.model.User;
import com.google.gson.Gson;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("auth")
public class AuthResource {

    @Context
    private UriInfo context;

    public AuthResource() {
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@FormParam("token") String token) {

        try {
            Token tokenAux = new Token(token);

            long currentTimeMS = System.currentTimeMillis();

            String userEmail = tokenAux.getClaim("email", Token.SECRET_KEY_AUTH).toString();
            String userPassword = tokenAux.getClaim("password", Token.SECRET_KEY_AUTH).toString();
            long expireTime = (long) tokenAux.getClaim("expireTime", Token.SECRET_KEY_AUTH);

            if (currentTimeMS > expireTime) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Credencial inválida.").build();
            } else {
                Auth auth = new Auth(userEmail, userPassword);
                int userId = auth.getUserId();

                if (userId > 0) {
                    return Response.ok(tokenAux.generateForRequest(userId)).build();
                }

                return Response.status(Response.Status.UNAUTHORIZED).entity("E-mail ou senha inválidos.").build();
            }
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Token inválido.").build();
        }
    }

    @POST
    @Path("/login_aux")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("email") String email, @FormParam("password") String password) {
        try {
            Gson gson = new Gson();
            
            Auth auth = new Auth(email, password);
            User user = auth.getUser();

            if (user != null) {
                return Response.ok(gson.toJson(user)).build();
            }

            return Response.status(Response.Status.UNAUTHORIZED).entity("E-mail ou senha inválidos.").build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao efetuar login.").build();
        }
    }
}

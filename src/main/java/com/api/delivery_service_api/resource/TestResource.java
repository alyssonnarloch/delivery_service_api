package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.auth.Token;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;


@Path("test")
public class TestResource {

    @Context
    private UriInfo context;

    public TestResource() {
    }


    @GET
    @Path("/token")
    @Produces("application/json")
    public String getTokenAuth(@QueryParam("email") String email, @QueryParam("password") String password) {
        Token token = new Token();
        //String value = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6ImFseXNzb24gbG9rbyIsInBhc3N3b3JkIjoiMTIzNDU2Nzg5MTAiLCJleHBpcmVUaW1lIjo4MDUyNH0.0ZPAIaYyThO_xNNqn4QMMBjh5D9r62rOB_FdLTCznXkKaoUw7uOyOmkHNkf5TPmbWD9NbIUvzLlMg9ePJqLquQ";
        //return token.authenticate(value);
        return token.generateForAuthentication(email, password);
    }


}

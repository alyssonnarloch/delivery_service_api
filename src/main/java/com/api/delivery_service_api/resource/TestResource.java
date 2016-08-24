package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.auth.Token;
import com.api.delivery_service_api.model.Task;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

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

    @GET
    @Path("/task/all")
    @Produces("application/json")
    public Response getAll() {
        Gson gson = new Gson();

        List<Task> tasks = new ArrayList();

        tasks.add(new Task(1, "Task 1"));
        tasks.add(new Task(2, "Task 2"));
        tasks.add(new Task(3, "Task 3"));
        tasks.add(new Task(4, "Task 4"));
        tasks.add(new Task(5, "Task 5"));

        return Response.status(Response.Status.OK).entity(gson.toJson(tasks)).build();
    }

    @POST
    @Path("/task/save")
    @Produces("application/json")
    public Response save(@FormParam("name") String name) {
        Gson gson = new Gson();

        Task task = new Task();
        task.setId(5);
        task.setName(name);

        return Response.status(Response.Status.OK).entity(gson.toJson(task)).build();
    }

}

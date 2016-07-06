package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.model.Client;
import com.api.delivery_service_api.model.City;
import com.google.gson.Gson;
import java.util.HashMap;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("client")
public class ClientResource {

    @Context
    private UriInfo context;

    public ClientResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("phone") String phone,
            @FormParam("zipcode") int zipCode,
            @FormParam("cityId") int cityId,
            @FormParam("address") String address,
            @FormParam("password") String password,
            @FormParam("profile_image") String profileImage) {
        
        Client client = new Client();
        client.setName(name);
        client.setEmail(email);
        client.setPhone(phone);
        client.setZipCode(zipCode);
        
        City city = new City();
        city.setId(cityId);
        client.setCity(city);
        
        client.setAddress(address);
        client.setPassword(password);
        client.setProfileImage(profileImage);
        
        HashMap<String, String> errors = client.getErrors();
        
        String errorsJson = "";
        Gson gson = new Gson();
        errorsJson = gson.toJson(errors);
        
        if(errors.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorsJson).build();
        }
        return Response.ok(errorsJson).build();
    }
}

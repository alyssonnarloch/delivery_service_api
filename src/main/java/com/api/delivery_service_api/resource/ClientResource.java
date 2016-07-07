package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
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
import org.hibernate.Session;
import org.hibernate.Transaction;

@Path("client")
public class ClientResource {

    @Context
    private UriInfo context;

    public ClientResource() {
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("phone") String phone,
            @FormParam("zipcode") int zipCode,
            @FormParam("cityId") int cityId,
            @FormParam("address") String address,
            @FormParam("number") int number,
            @FormParam("password") String password,
            @FormParam("profile_image") String profileImage) {

        Gson gson = new Gson();

        Client client = new Client();
        client.setName(name);
        client.setEmail(email);
        client.setPhone(phone);
        client.setZipCode(zipCode);

        City city = new City(cityId);
        client.setCity(city);

        client.setAddress(address);
        client.setNumber(number);
        client.setPassword(password);
        client.setProfileImage(profileImage);

        HashMap<String, String> errors = client.getErrors();

        if (errors.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(errors)).build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            s.save(client);

            t.commit();
            s.flush();
            s.close();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
        
        return Response.ok(gson.toJson(errors)).build();
    }
}

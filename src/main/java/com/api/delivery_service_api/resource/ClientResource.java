package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.Client;
import com.api.delivery_service_api.model.City;
import com.api.delivery_service_api.model.Project;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Path("client")
public class ClientResource {

    @Context
    private UriInfo context;

    public ClientResource() {
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        //Evita atualizaÃ§Ã£o automÃ¡tica das entidades
        s.setFlushMode(FlushMode.MANUAL);
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            Client client = (Client) s.get(Client.class, id);

            if (client == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("NÃ£o existe cliente associado ao id " + id + ".").build();
            }

            client.setPassword("**********************");

            t.commit();
            return Response.ok(gson.toJson(client)).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.flush();
            s.close();
        }
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("phone") String phone,
            @FormParam("zipcode") String zipCode,
            @FormParam("city_id") int cityId,
            @FormParam("address") String address,
            @FormParam("number") int number,
            @FormParam("password") String password,
            @FormParam("profile_image") String profileImage) {

        Gson gson = new Gson();

        HashMap<String, String> errors = new HashMap();
        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

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

        Set<ConstraintViolation<Client>> constraintViolations = validator.validate(client);

        for (ConstraintViolation<Client> c : constraintViolations) {
            String attrName = c.getPropertyPath().toString();
            
            if(attrName != null && attrName.isEmpty()) {
                attrName = c.getRootBeanClass().getSimpleName();
            }
            
            if (errors.get(attrName) != null) {
                errors.put(attrName, errors.get(attrName) + "/" + c.getMessage());
            } else {
                errors.put(attrName, c.getMessage());
            }
        }

        if (errors.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(errors)).build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            s.save(client);
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }

        return Response.ok(gson.toJson(errors)).build();
    }
}

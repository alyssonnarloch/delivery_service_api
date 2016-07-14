package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.extras.RESTDateParam;
import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import com.api.delivery_service_api.model.Client;
import com.api.delivery_service_api.model.Project;
import com.api.delivery_service_api.model.ProjectStatus;
import com.api.delivery_service_api.model.ServiceProvider;
import com.google.gson.Gson;
import java.util.HashMap;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

@Path("project")
public class ProjectResource {

    @Context
    private UriInfo context;

    public ProjectResource() {
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("client_id") int clientId,
            @FormParam("service_provider_id") int serviceProviderId,
            @FormParam("address") String address,
            @FormParam("number") int number,
            @FormParam("zip_code") String zipcode,
            @FormParam("city_id") int cityId,
            @FormParam("start_at") RESTDateParam startAt,
            @FormParam("end_at") RESTDateParam endAt) {

        Gson gson = new Gson();

        Project project = new Project();

        project.setClient(new Client(clientId));
        project.setSericeProvider(new ServiceProvider(serviceProviderId));
        project.setTitle(title);
        project.setDescription(description);
        project.setAddress(address);
        project.setNumber(number);
        project.setZipCode(zipcode);
        project.setCity(new City(cityId));
        project.setStartAt(startAt.getDate());
        project.setEndAt(endAt.getDate());
        project.setStatus(new ProjectStatus(1));

        HashMap<String, String> errors = project.getErrors();

        if (errors.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(errors)).build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            s.save(project);
            t.commit();
        } catch (ConstraintViolationException cve) {
            t.rollback();
            cve.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Cliente, Prestador de serviços ou Cidade inválidos.").build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }

        return Response.ok().build();
    }
}
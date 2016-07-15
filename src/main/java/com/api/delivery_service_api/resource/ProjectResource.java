package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.extras.RESTDateParam;
import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import com.api.delivery_service_api.model.Client;
import com.api.delivery_service_api.model.Project;
import com.api.delivery_service_api.model.ProjectStatus;
import com.api.delivery_service_api.model.ServiceProvider;
import com.api.delivery_service_api.modelaux.Period;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
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

        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

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

        Period period = new Period(startAt.getDate(), endAt.getDate());
        project.setPeriodDate(period);
        project.setStartAt(period.getStartAt());
        project.setEndAt(period.getEndAt());

        project.setStatus(new ProjectStatus(1));

        HashMap<String, String> errors = new HashMap();
        Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);

        for (ConstraintViolation<Project> c : constraintViolations) {
            String attrName = c.getPropertyPath().toString();

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

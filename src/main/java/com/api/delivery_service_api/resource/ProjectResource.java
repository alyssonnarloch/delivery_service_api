package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.custom_validation.ISave;
import com.api.delivery_service_api.custom_validation.IUpdate;
import com.api.delivery_service_api.extras.DateParam;
import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import com.api.delivery_service_api.model.Client;
import com.api.delivery_service_api.model.Project;
import com.api.delivery_service_api.model.ProjectStatus;
import com.api.delivery_service_api.model.ServiceProvider;
import com.api.delivery_service_api.modelaux.Period;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@Path("project")
public class ProjectResource {

    @Context
    private UriInfo context;

    public ProjectResource() {
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("client_id") int clientId,
            @FormParam("service_provider_id") int serviceProviderId,
            @FormParam("address") String address,
            @FormParam("number") int number,
            @FormParam("zip_code") String zipcode,
            @FormParam("city_id") int cityId,
            @FormParam("start_at") DateParam startAt,
            @FormParam("end_at") DateParam endAt) {

        Gson gson = new Gson();

        HashMap<String, String> errors = new HashMap();
        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

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

        Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project, ISave.class);

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

    @GET
    @Path("/client")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clientProjects(@QueryParam("client_id") int clientId,
            @QueryParam("status") int status) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            Criteria criteria = s.createCriteria(Project.class, "p")
                    .createAlias("status", "s")
                    .add(Restrictions.eq("p.client.id", clientId));

            if (status > 0) {
                criteria.add(Restrictions.eq("s.id", status));
            }

            //Resolve problema "failed to lazily initialize a collection of role" quando converte a lista em json na geração do response
            String projectsJson = gson.toJson(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list());

            s.clear();
            t.commit();

            return Response.ok(projectsJson).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }

    @GET
    @Path("/service_provider")
    @Produces(MediaType.APPLICATION_JSON)
    public Response serviceProviderProjects(@QueryParam("service_provider_id") int serviceProviderId,
            @QueryParam("status") int status) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            Criteria criteria = s.createCriteria(Project.class, "p")
                    .createAlias("status", "s")
                    .add(Restrictions.eq("p.serviceProvider.id", serviceProviderId));

            if (status > 0) {
                criteria.add(Restrictions.eq("s.id", status));
            }

            //Resolve problema "failed to lazily initialize a collection of role" quando converte a lista em json na geração do response
            String projectsJson = gson.toJson(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list());

            s.clear();
            t.commit();

            return Response.ok(projectsJson).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }

    @GET
    @Path("/service_provider/evaluations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response serviceProviderEvaluations(@QueryParam("service_provider_id") int serviceProviderId) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        List<Integer> status = new ArrayList();
        status.add(2);
        status.add(4);

        try {
            Criteria criteria = s.createCriteria(Project.class, "p")
                    .createAlias("status", "s")
                    .add(Restrictions.in("s.id", status))
                    .add(Restrictions.eq("p.serviceProvider.id", serviceProviderId))
                    .add(Restrictions.isNotNull("p.serviceProviderEvaluation"))
                    .add(Restrictions.isNotNull("p.serviceProviderQualification"));

            //Resolve problema "failed to lazily initialize a collection of role" quando converte a lista em json na geração do response            
            String projectsJson = gson.toJson(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list());

            s.clear();
            t.commit();

            return Response.ok(projectsJson).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }

    @GET
    @Path("/client/evaluations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clientEvaluations(@QueryParam("client_id") int clientId) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        List<Integer> status = new ArrayList();
        status.add(2);
        status.add(4);

        try {
            Criteria criteria = s.createCriteria(Project.class, "p")
                    .createAlias("status", "s")
                    .add(Restrictions.in("s.id", status))
                    .add(Restrictions.eq("p.client.id", clientId))
                    .add(Restrictions.isNotNull("p.clientEvaluation"))
                    .add(Restrictions.isNotNull("p.clientQualification"));

            //Resolve problema "failed to lazily initialize a collection of role" quando converte a lista em json na geração do response
            String projectsJson = gson.toJson(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list());

            s.clear();
            t.commit();

            return Response.ok(projectsJson).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@FormParam("project_id") int projectId,
            @FormParam("qualification") int qualification,
            @FormParam("description") String description,
            @FormParam("profile_id") int profileId,
            @FormParam("status") int status) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        //Evita atualizaÃ§Ã£o automÃ¡tica das entidades
//        s.setFlushMode(FlushMode.MANUAL);
        Transaction t = s.beginTransaction();

        HashMap<String, String> errors = new HashMap();
        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Gson gson = new Gson();

        try {
            Project project = (Project) s.get(Project.class, projectId);

            if (project == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Projeto não encontrado.").build();
            }

            project.setPeriodDate(new Period(project.getStartAt(), project.getEndAt()));
            project.setStatus(new ProjectStatus(status));

            //Cliente
            if (profileId == 1) {
                project.setClientEvaluation(description);
                project.setClientQualification(qualification);
            } else if (profileId == 2) {
                project.setServiceProviderEvaluation(description);
                project.setServiceProviderQualification(qualification);
            }

            Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project, IUpdate.class);

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

            s.update(project);

            s.flush();
            s.clear();
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/approve")
    @Produces(MediaType.APPLICATION_JSON)
    public Response approve(@FormParam("project_id") int projectId) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        //Evita atualizaÃ§Ã£o automÃ¡tica das entidades
//        s.setFlushMode(FlushMode.MANUAL);
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            Project project = (Project) s.get(Project.class, projectId);

            if (project == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Projeto não encontrado.").build();
            }

            project.setStatus(new ProjectStatus(3));

            s.update(project);

            s.flush();
            s.clear();
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        //Evita atualização automática das entidades
        //s.setFlushMode(FlushMode.MANUAL);
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            Project project = (Project) s.get(Project.class, id);

            if (project == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Não existe projeto associado ao id " + id + ".").build();
            }

            if (!t.isActive()) {
                s.clear();
                t.commit();
            }

            return Response.ok(gson.toJson(project)).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            t.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }
}

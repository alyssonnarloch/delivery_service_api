package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.Client;
import com.api.delivery_service_api.model.ClientServiceProviderFavorite;
import com.api.delivery_service_api.model.ProjectPortfolio;
import com.api.delivery_service_api.model.ServiceProvider;
import com.google.gson.Gson;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@Path("project_portfolio")
public class ProjectPortfolioResource {

    @Context
    private UriInfo context;

    public ProjectPortfolioResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@QueryParam("project_portfolio_id") int projectPortfolioId) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            ProjectPortfolio projectPortfolio = (ProjectPortfolio) s.get(ProjectPortfolio.class, projectPortfolioId);

            if (projectPortfolio == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Portifólio do projeto não encontrado.").build();
            }

            //s.flush();
            s.clear();
            t.commit();

            return Response.ok(projectPortfolio).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }

    @GET
    @Path("/project")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByProjectId(@QueryParam("project_id") int projectId) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            Criteria criteria = s.createCriteria(ProjectPortfolio.class, "pp")
                    .add(Restrictions.eq("pp.projectId", projectId));

            //Resolve problema "failed to lazily initialize a collection of role" quando converte a lista em json na geração do response
            String projectsJson = gson.toJson(criteria.list());

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response approveRejectImage(@QueryParam("project_portfolio_id") int projectPortfolioId,
            @QueryParam("approve") boolean newStatus) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            ProjectPortfolio projectPortfolio = (ProjectPortfolio) s.get(ProjectPortfolio.class, projectPortfolioId);

            if (projectPortfolio == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Portifólio do projeto não encontrado.").build();
            }

            projectPortfolio.setApproved(newStatus);
            s.save(projectPortfolio);

            s.flush();
            s.clear();
            t.commit();

            return Response.ok().build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("project_id") int projectId,
            @FormParam("image") String imageSrc) {
     Gson gson = new Gson();
        
        if (projectId == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Código do projeto inválido.").build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        s.setFlushMode(FlushMode.MANUAL);

        Transaction t = s.beginTransaction();

        try {
            ProjectPortfolio portfolio = new ProjectPortfolio();
            portfolio.setProjectId(projectId);
            portfolio.setImage(imageSrc);

            s.save(portfolio);

            s.flush();
            s.clear();
            t.commit();
            
            return Response.ok().entity(gson.toJson(portfolio)).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") int id) {
        if (id == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Id inválido.").build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            ProjectPortfolio portfolio = (ProjectPortfolio) s.get(ProjectPortfolio.class, id);

            s.delete(portfolio);

            s.flush();
            s.clear();
            t.commit();
        } catch (IllegalArgumentException iex) {
            t.rollback();
            iex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Portifólio não encontrado ou já foi excluído.").build();
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

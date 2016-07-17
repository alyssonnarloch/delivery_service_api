package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.ProjectPortfolio;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Session;
import org.hibernate.Transaction;

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

            t.commit();

            return Response.ok(projectPortfolio).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.flush();
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
            t.commit();

            return Response.ok().build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.flush();
            s.close();
        }
    }
}

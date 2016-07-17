package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.Project;
import com.api.delivery_service_api.model.ProjectStatus;
import com.api.delivery_service_api.model.ServiceProviderEvaluation;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@Path("service_provider/evaluation")
public class ServiceProviderEvaluationResource {

    @Context
    private UriInfo context;

    public ServiceProviderEvaluationResource() {
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("project_id") int projectId,
            @FormParam("qualification") int qualification,
            @FormParam("description") String description) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            Project project = (Project) s.get(Project.class, projectId);

            if (project == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Projeto não encontrado.").build();
            }

            Criteria criteria = s.createCriteria(ServiceProviderEvaluation.class, "spe")
                    .add(Restrictions.eq("spe.serviceProvider.id", project.getServiceProvider().getId()))
                    .add(Restrictions.eq("spe.client.id", project.getClient().getId()));

            if (criteria.list().size() > 1) {
                return Response.status(Response.Status.BAD_REQUEST).entity("O prestador de serviços desse projeto já foi avaliado por este cliente.").build();
            }

            
            ServiceProviderEvaluation evaluation = new ServiceProviderEvaluation();
            evaluation.setClient(null);
            
            project.setStatus(new ProjectStatus(4));
            
            t.commit();
        } catch (Exception ex) {

        } finally {

        }

        return Response.ok().build();
    }
}

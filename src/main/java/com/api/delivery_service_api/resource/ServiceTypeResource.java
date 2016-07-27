package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.auth.Token;
import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.ServiceType;
import com.google.gson.Gson;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

@Path("service_type")
public class ServiceTypeResource {

    @Context
    private UriInfo context;

    public ServiceTypeResource() {
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            Criteria criteria = s.createCriteria(ServiceType.class)
                    .addOrder(Order.asc("name"));

            List<ServiceType> serviceTypes = criteria.list();

            s.clear();
            t.commit();

            return Response.ok(gson.toJson(serviceTypes)).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }
}

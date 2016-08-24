package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import com.google.gson.Gson;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@Path("city")
public class CityResource {

    @Context
    private UriInfo context;

    public CityResource() {
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByName(@PathParam("name") String partName) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        //Evita atualização automática das entidades
        s.setFlushMode(FlushMode.MANUAL);

        Gson gson = new Gson();

        if (partName == null || partName.equals("") || partName.length() < 3) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Informe uma cidade com no mínimo 3 caracteres para efetuar a busca.").build();
        }

        try {
            Criteria criteria = s.createCriteria(City.class, "c")
                    .add(Restrictions.like("c.name", partName, MatchMode.ANYWHERE))
                    .addOrder(Order.asc("name"))
                    .setMaxResults(5);

            List<City> cities = criteria.list();

            return Response.ok(gson.toJson(cities)).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.flush();
            s.close();
        }
    }

}

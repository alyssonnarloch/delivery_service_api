package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.Client;
import com.api.delivery_service_api.model.ClientServiceProviderFavorite;
import com.api.delivery_service_api.model.ServiceProvider;
import com.google.gson.Gson;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@Path("favorite")
public class ClientServiceProviderFavoriteResource {

    @Context
    private UriInfo context;

    public ClientServiceProviderFavoriteResource() {
    }

    @GET
    @Path("{client_id}/{service_provider_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("client_id") int clientId,
            @PathParam("service_provider_id") int serviceProviderId) {

        if (clientId == 0 || serviceProviderId == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Código do cliente ou prestador de serviços inválido.").build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        s.setFlushMode(FlushMode.MANUAL);

        Transaction t = s.beginTransaction();

        Gson gson = new Gson();
        
        try {
            Criteria criteria = s.createCriteria(ClientServiceProviderFavorite.class, "favorite")
                    .add(Restrictions.eq("favorite.client.id", clientId))
                    .add(Restrictions.eq("favorite.serviceProvider.id", serviceProviderId));

            List<ClientServiceProviderFavorite> favorites = criteria.list();
            
            t.commit();
            
            if(favorites.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Código do cliente ou prestador de serviços inválido.").build();
            }
            
            return Response.ok(gson.toJson(favorites.get(0))).build();
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("client_id") int clientId,
            @FormParam("service_provider_id") int serviceProviderId) {

        if (clientId == 0 || serviceProviderId == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Código do cliente ou prestador de serviços inválido.").build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        s.setFlushMode(FlushMode.MANUAL);

        Transaction t = s.beginTransaction();

        try {
            ClientServiceProviderFavorite favorite = new ClientServiceProviderFavorite();
            favorite.setClient(new Client(clientId));
            favorite.setServiceProvider(new ServiceProvider(serviceProviderId));

            s.save(favorite);
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.flush();
            s.close();
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") int id) {
        if (id == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Id inválido.").build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            ClientServiceProviderFavorite favorite = (ClientServiceProviderFavorite) s.get(ClientServiceProviderFavorite.class, id);

            s.delete(favorite);
            t.commit();
        } catch (IllegalArgumentException iex) {
            t.rollback();
            iex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Prestador de serviços favorito não encontrado ou já foi excluído.").build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.flush();
            s.close();
        }

        return Response.ok().build();
    }
}

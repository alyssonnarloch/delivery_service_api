package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import com.api.delivery_service_api.model.ServiceProvider;
import com.api.delivery_service_api.model.ServiceProviderPortfolio;
import com.api.delivery_service_api.model.ServiceType;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

@Path("service_provider")
public class ServiceProviderResource {

    @Context
    private UriInfo context;

    public ServiceProviderResource() {
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@FormParam("name") String name,
            @FormParam("service_type") List<Integer> serviceTypeIds,
            @FormParam("city_id") int cityId,
            @FormParam("available") boolean available) {

        serviceTypeIds.removeAll(Arrays.asList("", null));

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            Criteria criteria = s.createCriteria(ServiceProvider.class, "sp")
                    .createAlias("servicesType", "st")
                    .createAlias("occupationAreas", "oa")
                    .createAlias("evaluation", "e")
                    .setProjection(Projections.projectionList()
                            .add(Projections.property("sp.name"), "name")
                            .add(Projections.groupProperty("sp.id"), "id")
                            .add(Projections.avg("e.qualification"), "qualificationAvg"))
                    .addOrder(Order.desc("qualificationAvg"))
                    .setResultTransformer(Transformers.aliasToBean(ServiceProvider.class));

            if (name != null && !name.equals("")) {
                criteria.add(Restrictions.like("sp.name", name, MatchMode.ANYWHERE));
            }

            if (!serviceTypeIds.isEmpty()) {
                criteria.add(Restrictions.in("st.id", serviceTypeIds));
            }

            if (cityId > 0) {
                criteria.add(Restrictions.eq("oa.id", cityId));
            }

            if (available) {
                criteria.add(Restrictions.eq("sp.available", available));
            }

            List<ServiceProvider> criteriaServiceProviders = criteria.list();
            List<ServiceProvider> servicesProvider = new ArrayList();

            for (ServiceProvider serviceProvider : criteriaServiceProviders) {
                ServiceProvider serviceProviderAux = (ServiceProvider) s.get(ServiceProvider.class, serviceProvider.getId());
                serviceProviderAux.setQualificationAvg(serviceProvider.getQualificationAvg());

                servicesProvider.add(serviceProviderAux);
            }

            t.commit();

            return Response.ok(gson.toJson(servicesProvider)).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.flush();
            s.close();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        //Evita atualização automática das entidades
        s.setFlushMode(FlushMode.MANUAL);
        //Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            ServiceProvider serviceProvider = (ServiceProvider) s.get(ServiceProvider.class, id);

            if (serviceProvider == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("NÃ£o existe prestador de serviÃ§o associado ao id " + id + ".").build();
            }

            serviceProvider.setPassword("**********************");

            //t.commit();
            return Response.ok(gson.toJson(serviceProvider)).build();
        } catch (Exception ex) {
            //t.rollback();
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
            @FormParam("profile_image") String profileImage,
            @FormParam("service_type") List<Integer> servicesTypeId,
            @FormParam("experience_description") String experienceDescription,
            @FormParam("available") boolean available,
            @FormParam("occupation_area") List<Integer> occupationAreas,
            @FormParam("profile_portfolio") List<String> profilePortfolio) {

        Gson gson = new Gson();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(name);
        serviceProvider.setEmail(email);
        serviceProvider.setPhone(phone);
        serviceProvider.setZipCode(zipCode);

        City city = new City(cityId);
        serviceProvider.setCity(city);

        serviceProvider.setAddress(address);
        serviceProvider.setNumber(number);
        serviceProvider.setPassword(password);
        serviceProvider.setProfileImage(profileImage);
        serviceProvider.setServiceTypeIds(servicesTypeId);
        serviceProvider.setExperienceDescription(experienceDescription);
        serviceProvider.setAvailable(available);
        serviceProvider.setOccupationAreaIds(occupationAreas);
        serviceProvider.setProfilePortfolioSrc(profilePortfolio);

        HashMap<String, String> errors = new HashMap();

        if (errors.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(errors)).build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            for (int serviceTypeId : servicesTypeId) {
                serviceProvider.addServiceType(new ServiceType(serviceTypeId));
            }

            for (int occupationAreaId : occupationAreas) {
                serviceProvider.addOccupationArea(new City(occupationAreaId));
            }

            for (String image : profilePortfolio) {
                serviceProvider.addPortfolio(new ServiceProviderPortfolio(image));
            }

            s.save(serviceProvider);
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

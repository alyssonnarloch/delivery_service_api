package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import com.api.delivery_service_api.model.Client;
import com.api.delivery_service_api.model.ServiceProvider;
import com.api.delivery_service_api.model.ServiceType;
import com.api.delivery_service_api.model.ServiceProviderServiceType;
import com.api.delivery_service_api.model.ServiceProviderOccupationArea;
import com.api.delivery_service_api.model.ServiceProviderPortfolio;
import com.google.gson.Gson;
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
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Path("service_provider")
public class ServiceProviderResource {

    @Context
    private UriInfo context;

    public ServiceProviderResource() {
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        //Evita atualização automática das entidades
        s.setFlushMode(FlushMode.MANUAL);
        Transaction t = s.beginTransaction();

        try {
            ServiceProvider serviceProvider = (ServiceProvider) s.get(ServiceProvider.class, id);

            if (serviceProvider == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Não existe prestador de serviço associado ao id " + id + ".").build();
            }

            serviceProvider.setPassword("**********************");

            Query queryServiceProviderServiceType = s.createQuery("FROM ServiceProviderServiceType WHERE service_provider_id = :service_provider_id");
            queryServiceProviderServiceType.setInteger("service_provider_id", id);

            List<ServiceProviderServiceType> serviceProviderServiceTypes = queryServiceProviderServiceType.list();

            for (ServiceProviderServiceType serviceProviderServiceType : serviceProviderServiceTypes) {
                serviceProvider.addServiceType(serviceProviderServiceType.getServiceType());
            }
            
            Query queryOccupationArea = s.createQuery("FROM ServiceProviderOccupationArea WHERE service_provider_id = :service_provider_id");
            queryOccupationArea.setInteger("service_provider_id", id);

            List<ServiceProviderOccupationArea> serviceProviderOccupationAreas = queryOccupationArea.list();

            for (ServiceProviderOccupationArea serviceProviderOccupationArea : serviceProviderOccupationAreas) {
                serviceProvider.addOccupationArea(serviceProviderOccupationArea.getCity());
            }

            t.commit();
            s.flush();
            s.close();

            return Response.ok(serviceProvider).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("phone") String phone,
            @FormParam("zipcode") int zipCode,
            @FormParam("city_id") int cityId,
            @FormParam("address") String address,
            @FormParam("number") int number,
            @FormParam("password") String password,
            @FormParam("profile_image") String profileImage,
            @FormParam("service_type") List<Integer> servicesType,
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
        serviceProvider.setServiceTypeIds(servicesType);
        serviceProvider.setExperienceDescription(experienceDescription);
        serviceProvider.setAvailable(available);
        serviceProvider.setOccupationAreaIds(occupationAreas);
        serviceProvider.setProfilePortfolioSrc(profilePortfolio);

        HashMap<String, String> errors = serviceProvider.getErrors();

        if (errors.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(errors)).build();
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            s.save(serviceProvider);

            for (int serviceTypeId : servicesType) {
                ServiceType serviceType = new ServiceType(serviceTypeId);

                ServiceProviderServiceType serviceProviderServiceType = new ServiceProviderServiceType();
                serviceProviderServiceType.setServiceProvider(serviceProvider);
                serviceProviderServiceType.setServiceType(serviceType);

                s.save(serviceProviderServiceType);
            }

            for (int occupationAreaId : occupationAreas) {
                City cityOccupation = new City(occupationAreaId);

                ServiceProviderOccupationArea serviceProviderOccupationArea = new ServiceProviderOccupationArea();
                serviceProviderOccupationArea.setCity(cityOccupation);
                serviceProviderOccupationArea.setServiceProivider(serviceProvider);

                s.save(serviceProviderOccupationArea);
            }

            for (String image : profilePortfolio) {
                ServiceProviderPortfolio serviceProviderPortfolio = new ServiceProviderPortfolio();
                serviceProviderPortfolio.setImage(image);
                serviceProviderPortfolio.setServiceProvider(serviceProvider);

                s.save(serviceProviderPortfolio);
            }

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

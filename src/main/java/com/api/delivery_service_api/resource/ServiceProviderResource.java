package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import com.api.delivery_service_api.model.ServiceProvider;
import com.api.delivery_service_api.model.ServiceType;
import com.api.delivery_service_api.model.ServiceProviderServiceType;
import com.api.delivery_service_api.model.ServiceProviderOccupationArea;
import com.api.delivery_service_api.model.ServiceProviderPortfolio;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Path("service_provider")
public class ServiceProviderResource {

    @Context
    private UriInfo context;

    public ServiceProviderResource() {
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("phone") String phone,
            @FormParam("zipcode") int zipCode,
            @FormParam("cityId") int cityId,
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
        serviceProvider.setServicesType(servicesType);
        serviceProvider.setExperienceDescription(experienceDescription);
        serviceProvider.setAvailable(available);
        serviceProvider.setOccupationAreas(occupationAreas);
        serviceProvider.setProfilePortfolio(profilePortfolio);

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
            s.flush();
            s.close();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

        return Response.ok(gson.toJson(errors)).build();
    }
}

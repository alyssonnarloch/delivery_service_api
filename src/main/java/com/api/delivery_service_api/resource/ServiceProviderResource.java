package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.custom_validation.ISave;
import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import com.api.delivery_service_api.model.Project;
import com.api.delivery_service_api.model.ServiceProvider;
import com.api.delivery_service_api.model.ServiceProviderPortfolio;
import com.api.delivery_service_api.model.ServiceType;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
            Criteria criteria = s.createCriteria(Project.class, "p")
                    .createAlias("serviceProvider", "sp")
                    .createAlias("sp.occupationAreas", "oa")
                    .createAlias("sp.servicesType", "st")
                    .setProjection(Projections.projectionList()
                            .add(Projections.property("sp.name"), "name")
                            .add(Projections.groupProperty("sp.id"), "id")
                            .add(Projections.avg("p.serviceProviderQualification"), "qualificationAvg"))
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

            s.clear();
            t.commit();

            return Response.ok(gson.toJson(servicesProvider)).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
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
            ServiceProvider serviceProvider = (ServiceProvider) s.get(ServiceProvider.class, id);

            if (serviceProvider == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Não existe prestador de serviço associado ao id " + id + ".").build();
            }

            serviceProvider.setPassword("**********************");

            s.clear();
            t.commit();

            return Response.ok(gson.toJson(serviceProvider)).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
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

        HashMap<String, String> errors = new HashMap();
        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

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

        Set<ConstraintViolation<ServiceProvider>> constraintViolations = validator.validate(serviceProvider, ISave.class);

        for (ConstraintViolation<ServiceProvider> c : constraintViolations) {
            String attrName = c.getPropertyPath().toString();

            if (attrName != null && attrName.isEmpty()) {
                attrName = c.getRootBeanClass().getSimpleName();
            }

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

        return Response.ok(gson.toJson(errors)).build();
    }
}

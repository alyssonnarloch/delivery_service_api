package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.custom_validation.ISave;
import com.api.delivery_service_api.custom_validation.IUpdateAreas;
import com.api.delivery_service_api.custom_validation.IUpdateMain;
import com.api.delivery_service_api.custom_validation.IUpdatePortfolio;
import com.api.delivery_service_api.custom_validation.IUpdateServices;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
                    .createAlias("sp.serviceTypes", "st")
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

    @PUT
    @Path("/edit/main")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editMain(@FormParam("id") int id,
            @FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("phone") String phone,
            @FormParam("zipcode") String zipCode,
            @FormParam("city_id") int cityId,
            @FormParam("address") String address,
            @FormParam("number") int number,
            @FormParam("profile_image") String profileImage) {

        Gson gson = new Gson();

        HashMap<String, String> errors = new HashMap();
        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            ServiceProvider serviceProvider = (ServiceProvider) s.get(ServiceProvider.class, id);

            if (serviceProvider == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Prestador de serviços não encontrado.").build();
            }

            serviceProvider.setName(name);
            serviceProvider.setEmail(email);
            serviceProvider.setPhone(phone);
            serviceProvider.setZipCode(zipCode);

            City city = new City(cityId);
            serviceProvider.setCity(city);

            serviceProvider.setAddress(address);
            serviceProvider.setNumber(number);
            serviceProvider.setProfileImage(profileImage);

            Set<ConstraintViolation<ServiceProvider>> constraintViolations = validator.validate(serviceProvider, IUpdateMain.class);

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

            s.update(serviceProvider);

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

        return Response.ok().build();
    }

    @PUT
    @Path("/edit/services")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editServices(@FormParam("id") int id,
            @FormParam("service_type") List<Integer> serviceTypesId,
            @FormParam("experience_description") String experienceDescription,
            @FormParam("available") boolean available) {

        Gson gson = new Gson();

        HashMap<String, String> errors = new HashMap();
        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            ServiceProvider serviceProvider = (ServiceProvider) s.get(ServiceProvider.class, id);

            if (serviceProvider == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Prestador de serviços não encontrado.").build();
            }

            serviceProvider.setServiceTypeIds(serviceTypesId);
            serviceProvider.setExperienceDescription(experienceDescription);
            serviceProvider.setAvailable(available);

            Set<ConstraintViolation<ServiceProvider>> constraintViolations = validator.validate(serviceProvider, IUpdateServices.class);

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

            serviceProvider.setserviceTypes(new HashSet());
            for (int serviceTypeId : serviceTypesId) {
                serviceProvider.addServiceType(new ServiceType(serviceTypeId));
            }

            s.update(serviceProvider);

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

        return Response.ok().build();
    }

    @PUT
    @Path("/edit/areas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editAreas(@FormParam("id") int id,
            @FormParam("occupation_area") List<Integer> occupationAreas) {

        Gson gson = new Gson();

        HashMap<String, String> errors = new HashMap();
        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            ServiceProvider serviceProvider = (ServiceProvider) s.get(ServiceProvider.class, id);

            if (serviceProvider == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Prestador de serviços não encontrado.").build();
            }

            serviceProvider.setOccupationAreaIds(occupationAreas);

            Set<ConstraintViolation<ServiceProvider>> constraintViolations = validator.validate(serviceProvider, IUpdateAreas.class);

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

            serviceProvider.setOccupationAreas(new HashSet());
            for (int occupationAreaId : occupationAreas) {
                serviceProvider.addOccupationArea(new City(occupationAreaId));
            }

            s.update(serviceProvider);

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

        return Response.ok().build();
    }

    @PUT
    @Path("/edit/portfolio")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editPortfolio(@FormParam("id") int id,
            @FormParam("profile_portfolio") List<String> profilePortfolio) {

        Gson gson = new Gson();

        HashMap<String, String> errors = new HashMap();
        Validator validator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        try {
            ServiceProvider serviceProvider = (ServiceProvider) s.get(ServiceProvider.class, id);

            if (serviceProvider == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Prestador de serviços não encontrado.").build();
            }

            serviceProvider.setProfilePortfolioSrc(profilePortfolio);

            Set<ConstraintViolation<ServiceProvider>> constraintViolations = validator.validate(serviceProvider, IUpdatePortfolio.class);

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

            Query query = s.createQuery("DELETE FROM ServiceProviderPortfolio WHERE service_provider_id = :service_provider_id");
            query.setInteger("service_provider_id", id);
            query.executeUpdate();

            serviceProvider.setPortfolio(new HashSet());
            for (String image : profilePortfolio) {
                ServiceProviderPortfolio serviceProviderPortfolio = new ServiceProviderPortfolio();
                serviceProviderPortfolio.setImage(image);
                serviceProviderPortfolio.setServiceProviderId(id);

                serviceProvider.addPortfolio(serviceProviderPortfolio);
            }

            s.update(serviceProvider);

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

        return Response.ok().build();
    }

    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
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
            @FormParam("service_type[]") List<Integer> serviceTypesId,
            @FormParam("experience_description") String experienceDescription,
            @FormParam("available") boolean available,
            @FormParam("occupation_area[]") List<Integer> occupationAreas,
            @FormParam("profile_portfolio[]") List<String> profilePortfolio) {

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
        serviceProvider.setServiceTypeIds(serviceTypesId);
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
            for (int serviceTypeId : serviceTypesId) {
                serviceProvider.addServiceType(new ServiceType(serviceTypeId));
            }

            for (int occupationAreaId : occupationAreas) {
                serviceProvider.addOccupationArea(new City(occupationAreaId));
            }

            s.save(serviceProvider);

            for (String image : profilePortfolio) {
                ServiceProviderPortfolio serviceProviderPortfolio = new ServiceProviderPortfolio();
                serviceProviderPortfolio.setServiceProviderId(serviceProvider.getId());
                serviceProviderPortfolio.setImage(image);

                s.save(serviceProviderPortfolio);
            }

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

    @GET
    @Path("/portfolio/{service_provider_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPortfolio(@PathParam("service_provider_id") int serviceProviderId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Gson gson = new Gson();

        try {
            String sql = "FROM ServiceProviderPortfolio WHERE service_provider_id = :service_provider_id";
            Query query = s.createQuery(sql);
            query.setInteger("service_provider_id", serviceProviderId);

            List<ServiceProviderPortfolio> portfolio = query.list();

            s.clear();
            t.commit();

            return Response.ok(gson.toJson(portfolio)).build();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } finally {
            s.close();
        }
    }
}

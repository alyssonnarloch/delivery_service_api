/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.auth.Token;
import com.api.delivery_service_api.hibernate.HibernateUtil;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.ServiceType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * REST Web Service
 *
 * @author Alysson Narloch
 */
@Path("service_type")
public class ServiceTypeResource {

    @Context
    private UriInfo context;

    public ServiceTypeResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
//        SessionFactory sf = HibernateUtil.getSessionFactory();
//        Session s = sf.openSession();
//        Transaction t = s.beginTransaction();
//        
        ServiceType type = new ServiceType();
//        //type.setName("Encanador");
//        
//        //s.save(type);
//        
//        t.commit();
//        
//        s.flush();
//        s.close();

        
        Token token = new Token();
        return token.generate();
        
        //return Response.ok(type).build();
    }
}

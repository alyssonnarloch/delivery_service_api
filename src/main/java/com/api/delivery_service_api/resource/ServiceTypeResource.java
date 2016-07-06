/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.delivery_service_api.resource;

import com.api.delivery_service_api.auth.Token;
import com.api.delivery_service_api.model.ServiceType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

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
        //String value = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6ImFseXNzb24gbG9rbyIsInBhc3N3b3JkIjoiMTIzNDU2Nzg5MTAiLCJleHBpcmVUaW1lIjo4MDUyNH0.0ZPAIaYyThO_xNNqn4QMMBjh5D9r62rOB_FdLTCznXkKaoUw7uOyOmkHNkf5TPmbWD9NbIUvzLlMg9ePJqLquQ";
        //return token.authenticate(value);
        return token.generateForAuthentication("alysson.narloch@gmail.com", "152036");
        
        //return Response.ok(type).build();
    }
}

package com.api.delivery_service_api.model;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Auth {

    private String email;
    private String password;

    public Auth() {
    }

    public Auth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserId() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();

        Query query = s.createQuery("FROM User WHERE email = :email AND password = :password");
        query.setString("email", this.getEmail());
        query.setString("password", this.getPassword());
        query.setMaxResults(1);

        User user = (User) query.uniqueResult();

        t.commit();
        s.flush();
        s.close();
        
        return user.getId();
    }
}

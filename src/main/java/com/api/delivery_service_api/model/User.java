package com.api.delivery_service_api.model;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.commons.validator.EmailValidator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Entity
@Table(name = "users")
@DiscriminatorColumn(name = "profile_id", discriminatorType = DiscriminatorType.INTEGER)
public class User implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String name;
    private String email;
    private String phone;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "zip_code")
    private int zipCode;

    private String address;
    private int number;

    @Column(name = "profile_image")
    private String profileImage;

    private String password;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean validEmail() {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(this.email);
    }
    
    public boolean hasEmail() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction t = s.beginTransaction();
        
        String sql = "SELECT COUNT(id) FROM User WHERE email = :email";
        
        if(this.getId() > 0) {
            sql = " AND id <> :id";
        }
        
        Query query = s.createQuery(sql);
        query.setString("email", this.getEmail());
        if(this.id > 0) {
            query.setInteger("id", this.getId());
        }

        Long numUsers = (Long) query.uniqueResult();
        
        t.commit();
        s.flush();
        s.close();
        
        return numUsers > 0;
    }
}

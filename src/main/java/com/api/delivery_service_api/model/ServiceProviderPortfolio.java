package com.api.delivery_service_api.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "service_provider_portfolio")
public class ServiceProviderPortfolio implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    private String image;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProvider;

    public ServiceProviderPortfolio() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

}

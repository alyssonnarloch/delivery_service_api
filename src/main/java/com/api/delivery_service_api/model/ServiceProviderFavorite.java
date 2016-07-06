package com.api.delivery_service_api.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "service_provider_favorites")
public class ServiceProviderFavorite implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProvider;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public ServiceProviderFavorite() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}

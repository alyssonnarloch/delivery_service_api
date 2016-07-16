package com.api.delivery_service_api.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class Client extends User {

    public Client() {
    }

    public Client(int id) {
        this.setId(id);
    }
}

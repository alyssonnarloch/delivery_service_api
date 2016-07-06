package com.api.delivery_service_api.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import com.api.delivery_service_api.model.User;

@Entity
@DiscriminatorValue("2")
public class ServiceProvider extends User {

    @Column(name = "experience_description")
    private String experienceDescription;
    private boolean available;

    public ServiceProvider() {
    }

    public String getExperienceDescription() {
        return experienceDescription;
    }

    public void setExperienceDescription(String experienceDescription) {
        this.experienceDescription = experienceDescription;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

}

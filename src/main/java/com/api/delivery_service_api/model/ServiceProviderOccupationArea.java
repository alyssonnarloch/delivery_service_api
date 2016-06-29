package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "occupation_areas")
public class ServiceProviderOccupationArea implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProivider;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    public ServiceProviderOccupationArea() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServiceProvider getServiceProivider() {
        return serviceProivider;
    }

    public void setServiceProivider(ServiceProvider serviceProivider) {
        this.serviceProivider = serviceProivider;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

}

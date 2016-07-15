package com.api.delivery_service_api.model;

import com.api.delivery_service_api.extras.Extra;
import com.google.common.base.Joiner;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "projects")
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min = 1)
    private String title;

    private String description;

    @Column(name = "start_at")
    private Date startAt;

    @Column(name = "end_at")
    private Date endAt;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider sericeProvider;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "zip_code")
    private String zipCode;
    private String address;
    private int number;

    @ManyToOne
    @JoinColumn(name = "status")
    private ProjectStatus status;

    public Project() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ServiceProvider getSericeProvider() {
        return sericeProvider;
    }

    public void setSericeProvider(ServiceProvider sericeProvider) {
        this.sericeProvider = sericeProvider;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public HashMap getErrors() {

        HashMap<String, String> errors = new HashMap();

        List<String> titleError = new ArrayList();
        List<String> descriptionError = new ArrayList();
        List<String> clientError = new ArrayList();
        List<String> serviceProviderError = new ArrayList();
        List<String> zipCodeError = new ArrayList();
        List<String> addressError = new ArrayList();
        List<String> numberError = new ArrayList();
        List<String> cityError = new ArrayList();
        List<String> startAtError = new ArrayList();
        List<String> endAtError = new ArrayList();

        Joiner joiner = Joiner.on("/");

        if (this.title == null || this.title.isEmpty()) {
            titleError.add("O título deve ser informado.");
        }

        if (this.description == null || this.description.isEmpty()) {
            descriptionError.add("A descrição deve ser informada.");
        }

        if (this.client.getId() <= 0) {
            serviceProviderError.add("Id do cliente inválido.");
        }

        if (this.sericeProvider.getId() <= 0) {
            serviceProviderError.add("Id do prestador de serviços inválido.");
        }

        if (this.zipCode == null || this.zipCode.isEmpty()) {
            zipCodeError.add("O CEP deve ser informado.");
        } else if (!Extra.zipCodeValid(this.zipCode)) {
            zipCodeError.add("CEP inválido.");
        }

        if (this.address == null || this.address.isEmpty()) {
            addressError.add("O endereço deve ser informado.");
        }

        if (this.number <= 0) {
            numberError.add("Número inválido.");
        }

        if (this.city == null || this.city.getId() <= 0) {
            cityError.add("Cidade inválida.");
        }

        if (this.startAt == null) {
            startAtError.add("Data de início deve ser informada.");
        }

        if (this.endAt == null) {
            endAtError.add("Data de término deve ser informada.");
        }

        if (titleError.size() > 0) {
            errors.put("title", joiner.join(titleError));
        }

        if (descriptionError.size() > 0) {
            errors.put("description", joiner.join(descriptionError));
        }

        if (clientError.size() > 0) {
            errors.put("client", joiner.join(clientError));
        }

        if (serviceProviderError.size() > 0) {
            errors.put("service_provider", joiner.join(serviceProviderError));
        }

        if (zipCodeError.size() > 0) {
            errors.put("zip_code", joiner.join(zipCodeError));
        }

        if (addressError.size() > 0) {
            errors.put("address", joiner.join(addressError));
        }

        if (numberError.size() > 0) {
            errors.put("number", joiner.join(numberError));
        }

        if (cityError.size() > 0) {
            errors.put("city", joiner.join(cityError));
        }

        if (startAtError.size() > 0) {
            errors.put("start_at", joiner.join(startAtError));
        }

        if (endAtError.size() > 0) {
            errors.put("end_at", joiner.join(endAtError));
        }

        return errors;
    }
}

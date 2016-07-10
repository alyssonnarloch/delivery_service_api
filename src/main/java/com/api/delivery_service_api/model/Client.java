package com.api.delivery_service_api.model;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
@DiscriminatorValue("1")
public class Client extends User {

    @ManyToMany
    @JoinTable(
            name = "service_provider_favorites",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "service_provider_id"))
    private List<ServiceProvider> servicesProviderFavorites;

    public Client() {
        this.servicesProviderFavorites = new ArrayList();
    }

    public List<ServiceProvider> getServicesProviderFavorites() {
        return servicesProviderFavorites;
    }

    public void setServicesProviderFavorites(List<ServiceProvider> servicesProviderFavorites) {
        this.servicesProviderFavorites = servicesProviderFavorites;
    }

    public HashMap getErrors() {

        HashMap<String, String> errors = new HashMap();
        List<String> nameError = new ArrayList<>();
        List<String> emailError = new ArrayList<>();

        Joiner joiner = Joiner.on("/");

        if (this.getName() == null || this.getName().isEmpty()) {
            nameError.add("Nome deve ser informado");
        }

        if (this.getEmail() == null || this.getEmail().isEmpty()) {
            emailError.add("E-mail deve ser informado.");
        } else if (!this.validEmail()) {
            emailError.add("Formato de e-mail inválido.");
        } else if (this.hasEmail()) {
            emailError.add("E-mail já cadastrado por outro usuário.");
        }

        if (nameError.size() > 0) {
            errors.put("name", joiner.join(nameError));
        }
        if (emailError.size() > 0) {
            errors.put("email", joiner.join(emailError));
        }

        return errors;
    }
}

package com.api.delivery_service_api.model;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("2")
public class ServiceProvider extends User {

    @Column(name = "experience_description", length = 1500)
    private String experienceDescription;
    private boolean available;

    @Transient
    private List<Integer> servicesType;

    @Transient
    private List<Integer> occupationAreas;

    @Transient
    private List<String> profilePortfolio;

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

    public List<Integer> getServicesType() {
        return servicesType;
    }

    public void setServicesType(List<Integer> servicesType) {
        this.servicesType = servicesType;
    }

    public List<Integer> getOccupationAreas() {
        return occupationAreas;
    }

    public void setOccupationAreas(List<Integer> occupationAreas) {
        this.occupationAreas = occupationAreas;
    }

    public List<String> getProfilePortfolio() {
        return profilePortfolio;
    }

    public void setProfilePortfolio(List<String> profilePortfolio) {
        this.profilePortfolio = profilePortfolio;
    }

    public HashMap getErrors() {

        HashMap<String, String> errors = new HashMap();

        List<String> nameError = new ArrayList<>();
        List<String> emailError = new ArrayList<>();
        List<String> servicesTypeError = new ArrayList<>();
        List<String> occupationAreasError = new ArrayList<>();
        List<String> profilePortfolioError = new ArrayList<>();

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

        if (this.getServicesType().isEmpty()) {
            servicesTypeError.add("Deve ser informado ao menos um tipo de serviço.");
        }

        if (this.getOccupationAreas().isEmpty()) {
            occupationAreasError.add("Deve ser informado ao menos uma área de atuação.");
        }

        if (this.getProfilePortfolio().isEmpty()) {
            profilePortfolioError.add("Deve ser adicionada ao menos uma imagem ao portifólio.");
        }

        if (nameError.size() > 0) {
            errors.put("name", joiner.join(nameError));
        }
        if (emailError.size() > 0) {
            errors.put("email", joiner.join(emailError));
        }
        if (servicesTypeError.size() > 0) {
            errors.put("service_type", joiner.join(servicesTypeError));
        }
        if (occupationAreasError.size() > 0) {
            errors.put("occupation_area", joiner.join(occupationAreasError));
        }
        if (profilePortfolioError.size() > 0) {
            errors.put("profile_portfolio", joiner.join(profilePortfolioError));
        }

        return errors;
    }
}

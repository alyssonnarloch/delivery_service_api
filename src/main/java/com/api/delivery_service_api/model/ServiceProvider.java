package com.api.delivery_service_api.model;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("2")
public class ServiceProvider extends User {

    @Column(name = "experience_description", length = 1500)
    private String experienceDescription;
    private boolean available;

    @Transient
    private List<Integer> serviceTypeIds;

    @Transient
    private List<Integer> occupationAreaIds;

    @Transient
    private List<String> profilePortfolioSrc;

    @ManyToMany
    @JoinTable(
            name = "service_provider_service_types",
            joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id"))
    private List<ServiceType> servicesType;

    @ManyToMany
    @JoinTable(
            name = "service_provider_occupation_areas",
            joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id"))
    private List<City> occupationAreas;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_provider_id")
    private List<ServiceProviderPortfolio> portfolio;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_provider_id")
    private List<ServiceProviderEvaluation> evaluation;

    @Transient
    private double qualificationAvg;

    public ServiceProvider() {
        this.serviceTypeIds = new ArrayList();
        this.occupationAreaIds = new ArrayList();
        this.profilePortfolioSrc = new ArrayList();
        this.servicesType = new ArrayList();
        this.occupationAreas = new ArrayList();
        this.portfolio = new ArrayList();
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

    public List<ServiceProviderEvaluation> getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(List<ServiceProviderEvaluation> evaluation) {
        this.evaluation = evaluation;
    }

    public List<Integer> getServiceTypeIds() {
        return serviceTypeIds;
    }

    public void setServiceTypeIds(List<Integer> serviceTypeIds) {
        serviceTypeIds.removeAll(Arrays.asList("", null));
        this.serviceTypeIds = serviceTypeIds;
    }

    public List<Integer> getOccupationAreaIds() {
        occupationAreaIds.removeAll(Arrays.asList("", null));
        return occupationAreaIds;
    }

    public void setOccupationAreaIds(List<Integer> occupationAreaIds) {
        this.occupationAreaIds = occupationAreaIds;
    }

    public List<String> getProfilePortfolioSrc() {
        return profilePortfolioSrc;
    }

    public void setProfilePortfolioSrc(List<String> profilePortfolioSrc) {
        profilePortfolioSrc.removeAll(Arrays.asList("", null));
        this.profilePortfolioSrc = profilePortfolioSrc;
    }

    public List<ServiceType> getServicesType() {
        return servicesType;
    }

    public void setServicesType(List<ServiceType> servicesType) {
        this.servicesType = servicesType;
    }

    public List<City> getOccupationAreas() {
        return occupationAreas;
    }

    public double getQualificationAvg() {
        return qualificationAvg;
    }

    public void setQualificationAvg(double qualificationAvg) {
        this.qualificationAvg = qualificationAvg;
    }

    public void setOccupationAreas(List<City> occupationAreas) {
        this.occupationAreas = occupationAreas;
    }

    public List<ServiceProviderPortfolio> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(List<ServiceProviderPortfolio> portfolio) {
        this.portfolio = portfolio;
    }

    public void addServiceType(ServiceType serviceType) {
        this.servicesType.add(serviceType);
    }

    public void addOccupationArea(City city) {
        this.occupationAreas.add(city);
    }

    public void addPortfolio(ServiceProviderPortfolio portfolio) {
        this.portfolio.add(portfolio);
    }

    public HashMap getErrors() {

        HashMap<String, String> errors = new HashMap();

        List<String> nameError = new ArrayList();
        List<String> emailError = new ArrayList();
        List<String> serviceTypeIdsError = new ArrayList();
        List<String> occupationAreaIdsError = new ArrayList();
        List<String> profilePortfolioSrcError = new ArrayList();

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

        if (this.getServiceTypeIds().isEmpty()) {
            serviceTypeIdsError.add("Deve ser informado ao menos um tipo de serviço.");
        }

        if (this.getOccupationAreaIds().isEmpty()) {
            occupationAreaIdsError.add("Deve ser informado ao menos uma área de atuação.");
        }

        if (this.getProfilePortfolioSrc().isEmpty()) {
            profilePortfolioSrcError.add("Deve ser adicionada ao menos uma imagem ao portifólio.");
        }

        if (nameError.size() > 0) {
            errors.put("name", joiner.join(nameError));
        }
        if (emailError.size() > 0) {
            errors.put("email", joiner.join(emailError));
        }
        if (serviceTypeIdsError.size() > 0) {
            errors.put("service_type", joiner.join(serviceTypeIdsError));
        }
        if (occupationAreaIdsError.size() > 0) {
            errors.put("occupation_area", joiner.join(occupationAreaIdsError));
        }
        if (profilePortfolioSrcError.size() > 0) {
            errors.put("profile_portfolio", joiner.join(profilePortfolioSrcError));
        }

        return errors;
    }
}

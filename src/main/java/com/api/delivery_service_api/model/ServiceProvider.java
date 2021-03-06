package com.api.delivery_service_api.model;

import com.api.delivery_service_api.custom_validation.IListNotEmpty;
import com.api.delivery_service_api.custom_validation.INotEmpty;
import com.api.delivery_service_api.custom_validation.ISave;
import com.api.delivery_service_api.custom_validation.IUpdateAreas;
import com.api.delivery_service_api.custom_validation.IUpdatePortfolio;
import com.api.delivery_service_api.custom_validation.IUpdateServices;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("2")
public class ServiceProvider extends User {

    @Column(name = "experience_description", length = 1500)
    @INotEmpty(message = "A experiência deve ser informada.", groups = {ISave.class})
    private String experienceDescription;
    
    private boolean available;

    @Transient
    @IListNotEmpty(message = "Os tipos de serviço devem ser informados.", groups = {ISave.class, IUpdateServices.class})
    private List<Integer> serviceTypeIds;

    @Transient
    @IListNotEmpty(message = "As áreas de atuação devem ser informadas.", groups = {ISave.class, IUpdateAreas.class})
    private List<Integer> occupationAreaIds;

    @Transient
    @IListNotEmpty(message = "As imagens do portifólio devem ser adicionadas.", groups = {ISave.class, IUpdatePortfolio.class})
    private List<String> profilePortfolioSrc;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "service_provider_service_types",
            joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id"))
    private Set<ServiceType> serviceTypes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "service_provider_occupation_areas",
            joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id"))
    private Set<City> occupationAreas;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "service_provider_id")
    private Set<ServiceProviderPortfolio> portfolio;

    @Transient
    private Double qualificationAvg;

    public ServiceProvider() {
        this.serviceTypeIds = new ArrayList();
        this.occupationAreaIds = new ArrayList();
        this.profilePortfolioSrc = new ArrayList();
        this.serviceTypes = new HashSet();
        this.occupationAreas = new HashSet();
        this.portfolio = new HashSet();
    }

    public ServiceProvider(int id) {
        this.setId(id);
        this.serviceTypeIds = new ArrayList();
        this.occupationAreaIds = new ArrayList();
        this.profilePortfolioSrc = new ArrayList();
        this.serviceTypes = new HashSet();
        this.occupationAreas = new HashSet();
        this.portfolio = new HashSet();
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
        occupationAreaIds.removeAll(Arrays.asList("", null));
        this.occupationAreaIds = occupationAreaIds;
    }

    public List<String> getProfilePortfolioSrc() {
        return profilePortfolioSrc;
    }

    public void setProfilePortfolioSrc(List<String> profilePortfolioSrc) {
        profilePortfolioSrc.removeAll(Arrays.asList("", null));
        this.profilePortfolioSrc = profilePortfolioSrc;
    }

    public Set<ServiceType> getserviceTypes() {
        return serviceTypes;
    }

    public void setserviceTypes(Set<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public Set<City> getOccupationAreas() {
        return occupationAreas;
    }

    public Double getQualificationAvg() {
        return qualificationAvg;
    }

    public void setQualificationAvg(Double qualificationAvg) {
        this.qualificationAvg = qualificationAvg;
    }

    public void setOccupationAreas(Set<City> occupationAreas) {
        this.occupationAreas = occupationAreas;
    }

    public Set<ServiceProviderPortfolio> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Set<ServiceProviderPortfolio> portfolio) {
        this.portfolio = portfolio;
    }

    public void addServiceType(ServiceType serviceType) {
        this.serviceTypes.add(serviceType);
    }

    public void addOccupationArea(City city) {
        this.occupationAreas.add(city);
    }

    public void addPortfolio(ServiceProviderPortfolio portfolio) {
        this.portfolio.add(portfolio);
    }
}

package com.api.delivery_service_api.model;

import com.api.delivery_service_api.custom_validation.ICity;
import com.api.delivery_service_api.custom_validation.IClient;
import com.api.delivery_service_api.custom_validation.IDatePeriod;
import com.api.delivery_service_api.custom_validation.INotEmpty;
import com.api.delivery_service_api.custom_validation.IServiceProvider;
import com.api.delivery_service_api.custom_validation.IZipCode;
import com.api.delivery_service_api.modelaux.Period;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "projects")
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @INotEmpty(message = "O título deve ser informado.")
    @Size(min = 5, message = "Título inválido.")
    private String title;

    @INotEmpty(message = "A descrição eve ser informada.")
    @Size(min = 5, message = "Descrição inválida.")
    private String description;

    @Column(name = "start_at")
    @NotNull(message = "A data de início deve ser informada.")
    private Date startAt;

    @Column(name = "end_at")
    @NotNull(message = "A data de término deve ser informada.")
    private Date endAt;

    @Column(name = "service_provider_evaluation")
    private String serviceProviderEvaluation;

    @Column(name = "client_evaluation")
    private String clientEvaluation;

    @Column(name = "service_provider_qualification")
    private int serviceProviderQualification;

    @Column(name = "client_qualification")
    private int clientQualification;

    @Transient
    @IDatePeriod(message = "Período de datas inválido.")
    private Period periodDate;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @NotNull(message = "O cliente deve ser indormado.")
    @IClient(message = "Cliente inválido.")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    @NotNull(message = "O prestador de serviços deve ser indormado.")
    @IServiceProvider(message = "Prestador de serviços inválido.")
    private ServiceProvider serviceProvider;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @NotNull(message = "A cidade deve ser informada.")
    @ICity(message = "Cidade inválida.")
    private City city;

    @Column(name = "zip_code")
    @INotEmpty(message = "O CEP deve ser informado.")
    @IZipCode(message = "CEP inválido.")
    private String zipCode;

    @INotEmpty(message = "O endereço deve ser informado.")
    @Size(min = 5, message = "Endereço inválido.")
    private String address;

    @Min(value = 1, message = "Número inválido.")
    private int number;

    @ManyToOne
    @JoinColumn(name = "status")
    private ProjectStatus status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private List<ProjectPortfolio> portfolio;

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

    public Period getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Period periodDate) {
        this.periodDate = periodDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setSericeProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
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

    public List<ProjectPortfolio> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(List<ProjectPortfolio> portfolio) {
        this.portfolio = portfolio;
    }

    public String getServiceProviderEvaluation() {
        return serviceProviderEvaluation;
    }

    public void setServiceProviderEvaluation(String serviceProviderEvaluation) {
        this.serviceProviderEvaluation = serviceProviderEvaluation;
    }

    public String getClientEvaluation() {
        return clientEvaluation;
    }

    public void setClientEvaluation(String clientEvaluation) {
        this.clientEvaluation = clientEvaluation;
    }

    public int getServiceProviderQualification() {
        return serviceProviderQualification;
    }

    public void setServiceProviderQualification(int serviceProviderQualification) {
        this.serviceProviderQualification = serviceProviderQualification;
    }

    public int getClientQualification() {
        return clientQualification;
    }

    public void setClientQualification(int clientQualification) {
        this.clientQualification = clientQualification;
    }

}

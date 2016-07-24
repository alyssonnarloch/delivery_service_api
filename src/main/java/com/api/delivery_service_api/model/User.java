package com.api.delivery_service_api.model;

import com.api.delivery_service_api.custom_validation.ICity;
import com.api.delivery_service_api.custom_validation.IEmail;
import com.api.delivery_service_api.custom_validation.INotEmpty;
import com.api.delivery_service_api.custom_validation.ISave;
import com.api.delivery_service_api.custom_validation.IUpdateMain;
import com.api.delivery_service_api.custom_validation.IUserNotExists;
import com.api.delivery_service_api.custom_validation.IZipCode;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@DiscriminatorColumn(name = "profile_id", discriminatorType = DiscriminatorType.INTEGER)
@IUserNotExists(message = "E-mail já utilizado por outro usuário.", groups = {ISave.class, IUpdateMain.class})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @INotEmpty(message = "O nome deve ser informado.", groups = {ISave.class, IUpdateMain.class})
    @Size(min = 5, message = "Nome inválido.", groups = {ISave.class, IUpdateMain.class})
    private String name;

    @INotEmpty(message = "O e-mail deve ser informado.", groups = {ISave.class, IUpdateMain.class})
    @IEmail(message = "E-mail inválido.", groups = {ISave.class, IUpdateMain.class})
    private String email;

    @INotEmpty(message = "O telefone deve ser informado.", groups = {ISave.class, IUpdateMain.class})
    private String phone;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @ICity(message = "Cidade inválida.", groups = {ISave.class, IUpdateMain.class})
    private City city;

    @Column(name = "zip_code")
    @INotEmpty(message = "O CEP deve ser informado.", groups = {ISave.class, IUpdateMain.class})
    @IZipCode(message = "CEP inválido.", groups = {ISave.class, IUpdateMain.class})
    private String zipCode;

    @INotEmpty(message = "O endereço deve ser informado.", groups = {ISave.class, IUpdateMain.class})
    @Size(min = 5, message = "Endereço inválido.", groups = {ISave.class, IUpdateMain.class})
    private String address;

    @Min(value = 1, message = "Número inválido.", groups = {ISave.class, IUpdateMain.class})
    private int number;

    @Column(name = "profile_image", length = 1500)
    @INotEmpty(message = "A imagem do perfil deve ser informada.", groups = {ISave.class, IUpdateMain.class})
    private String profileImage;

    @INotEmpty(message = "A senha deve ser informada.", groups = {ISave.class})
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.", groups = {ISave.class})
    private String password;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getProfileId() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }

   
}

package com.api.delivery_service_api.model;

import java.util.HashMap;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class Client extends User {

    public Client() {
    }

    public HashMap validate() {

        HashMap<String, String> errors = new HashMap();
        String emailError = "";

        if (!this.validEmail()) {
            emailError += "Formato de e-mail inválido.";
        }

        errors.put("email", emailError);

        return errors;
    }
}

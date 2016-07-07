package com.api.delivery_service_api.model;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class Client extends User {

    public Client() {
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

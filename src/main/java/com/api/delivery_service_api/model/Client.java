package model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import model.User;

@Entity
@DiscriminatorValue("1")
public class Client extends User {

    public Client() {
    }

}

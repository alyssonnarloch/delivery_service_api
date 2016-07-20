package com.api.delivery_service_api.custom_validation;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.Client;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClientValidator implements ConstraintValidator<IClient, Client> {

    private Session s;
    private Transaction t;

    @Override
    public void initialize(IClient a) {
        this.s = HibernateUtil.getSessionFactory().openSession();
        this.t = this.s.beginTransaction();
    }

    @Override
    public boolean isValid(Client c, ConstraintValidatorContext cvc) {
        try {
            Client client = (Client) s.get(Client.class, c.getId());

            this.s.flush();
            this.s.clear();
            this.t.commit();

            if (client != null) {
                return true;
            }
        } catch (Exception ex) {
            this.t.rollback();
        } finally {
            this.s.close();
        }

        return false;
    }

}

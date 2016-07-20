package com.api.delivery_service_api.custom_validation;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.ServiceProvider;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ServiceProviderValidator implements ConstraintValidator<IServiceProvider, ServiceProvider> {

    private Session s;
    private Transaction t;

    @Override
    public void initialize(IServiceProvider a) {
        this.s = HibernateUtil.getSessionFactory().openSession();
        this.t = this.s.beginTransaction();
    }

    @Override
    public boolean isValid(ServiceProvider sp, ConstraintValidatorContext cvc) {
        try {
            ServiceProvider serviceProvider = (ServiceProvider) s.get(ServiceProvider.class, sp.getId());

            this.s.flush();
            this.s.clear();
            this.t.commit();

            if (serviceProvider != null) {
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

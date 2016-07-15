package com.api.delivery_service_api.custom_validation;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.City;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CityValidator implements ConstraintValidator<ICity, City> {

    private Session s;
    private Transaction t;

    @Override
    public void initialize(ICity a) {
        this.s = HibernateUtil.getSessionFactory().openSession();
        this.t = this.s.beginTransaction();
    }

    @Override
    public boolean isValid(City c, ConstraintValidatorContext cvc) {
        try {
            City city = (City) s.get(City.class, c.getId());

            this.t.commit();

            if (city != null) {
                return true;
            }
        } catch (Exception ex) {
            this.t.rollback();
        } finally {
            this.s.flush();
            this.s.close();
        }

        return false;
    }

}

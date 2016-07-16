package com.api.delivery_service_api.custom_validation;

import com.api.delivery_service_api.hibernate.HibernateUtil;
import com.api.delivery_service_api.model.User;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class UserNotExistsValidator implements ConstraintValidator<IUserNotExists, User> {

    private Session s;
    private Transaction t;

    @Override
    public void initialize(IUserNotExists constraintAnnotation) {
        this.s = HibernateUtil.getSessionFactory().openSession();
        this.t = this.s.beginTransaction();
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        try {
            Criteria criteria = this.s.createCriteria(User.class)
                    .add(Restrictions.eq("email", user.getEmail()))                    
                    .setProjection(Projections.projectionList()
                            .add(Projections.property("id")));

            if(user.getId() > 0) {
                criteria.add(Restrictions.ne("id", user.getId()));
            }
            
            
            int numUsers = criteria.list().size();

            this.t.commit();

            if (numUsers > 0) {
                return false;
            }
        } catch (Exception ex) {
            this.t.rollback();
            ex.printStackTrace();
            return false;
        }

        return true;
    }

}

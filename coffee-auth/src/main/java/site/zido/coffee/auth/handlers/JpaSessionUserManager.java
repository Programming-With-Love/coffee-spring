package site.zido.coffee.auth.handlers;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import site.zido.coffee.auth.entity.IUser;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class JpaSessionUserManager extends AbstractSessionUserManager
        implements InitializingBean {
    private EntityManager entityManager;

    @Override
    protected IUser getUserByKey(Object fieldValue, String fieldName, Class<? extends IUser> userClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<? extends IUser> query = criteriaBuilder.createQuery(userClass);
        Root<? extends IUser> root = query.from(userClass);
        Predicate pre = criteriaBuilder.equal(root.get(fieldName), fieldValue);
        query = query.where(pre);
        TypedQuery<? extends IUser> typedQuery = entityManager.createQuery(query);
        return typedQuery.getSingleResult();
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(entityManager, "entity manager can't be null");
    }
}

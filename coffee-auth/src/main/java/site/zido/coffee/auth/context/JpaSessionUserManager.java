package site.zido.coffee.auth.context;

import org.springframework.util.Assert;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.user.IUser;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 使用jpa/hibernate作为orm的用户管理器
 *
 * @author zido
 */
public class JpaSessionUserManager extends AbstractSessionUserManager {
    private EntityManager em;

    public JpaSessionUserManager(EntityManager em) {
        Assert.notNull(em, "the entity manager can't be null");
        this.em = em;
    }

    @Override
    protected Authentication getUserByKey(Object fieldValue, String fieldName, Class<? extends IUser> userClass) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<? extends IUser> query = criteriaBuilder.createQuery(userClass);
        Root<? extends IUser> root = query.from(userClass);
        Predicate pre = criteriaBuilder.equal(root.get(fieldName), fieldValue);
        query = query.where(pre);
        TypedQuery<? extends IUser> typedQuery = em.createQuery(query);
        //TODO
//        return typedQuery.getSingleResult();
        return null;
    }
}

package site.zido.coffee.auth.user;

import site.zido.coffee.auth.authentication.UsernameNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 由hibernate框架支撑的用户查询服务
 *
 * @author zido
 */
public class JpaSupportedUserServiceImpl implements IUserService {
    private EntityManager em;

    public JpaSupportedUserServiceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Object loadUser(Object fieldValue, String fieldName, Class<?> userClass) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<?> query = criteriaBuilder.createQuery(userClass);
        Root<?> root = query.from(userClass);
        Predicate pre = criteriaBuilder.equal(root.get(fieldName), fieldValue);
        query = query.where(pre);
        TypedQuery<?> typedQuery = em.createQuery(query);
        return typedQuery.getSingleResult();
    }
}

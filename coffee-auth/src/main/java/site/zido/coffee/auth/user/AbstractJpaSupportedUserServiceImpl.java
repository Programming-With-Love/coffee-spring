package site.zido.coffee.auth.user;

import org.springframework.beans.factory.annotation.Autowired;

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
public abstract class AbstractJpaSupportedUserServiceImpl<T extends IUser> implements IUserService<T> {
    private EntityManager em;
    private Class<?> userClass;
    private String fieldName;

    public AbstractJpaSupportedUserServiceImpl(Class<?> userClass, String fieldName, EntityManager em) {
        this.userClass = userClass;
        this.fieldName = fieldName;
        this.em = em;
    }

    @Override
    public T loadUser(Object fieldValue) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<?> query = criteriaBuilder.createQuery(userClass);
        Root<?> root = query.from(userClass);
        Predicate pre = criteriaBuilder.equal(root.get(fieldName), fieldValue);
        query = query.where(pre);
        TypedQuery<?> typedQuery = em.createQuery(query);
        return packageUser(typedQuery.getSingleResult());
    }

    protected abstract T packageUser(Object userEntity);
}

package site.zido.demo.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.demo.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

    List<User> findAllByIdIn(List<Integer> ids, Sort createTime);
}

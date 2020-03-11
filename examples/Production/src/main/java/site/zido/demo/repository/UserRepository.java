package site.zido.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.common.model.BaseRepository;
import site.zido.demo.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Integer>, JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

   Optional<User> findByPhone(String phone);
}

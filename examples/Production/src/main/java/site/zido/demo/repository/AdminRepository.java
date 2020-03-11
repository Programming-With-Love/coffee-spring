package site.zido.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.demo.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin,String> {
}

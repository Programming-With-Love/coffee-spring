package site.zido.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.demo.entity.Record;

public interface RecordRepository extends JpaRepository<Record, Integer> {
}

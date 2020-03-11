package site.zido.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.common.model.BaseRepository;
import site.zido.demo.entity.Record;

public interface RecordRepository extends BaseRepository<Record, Integer>, JpaRepository<Record, Integer> {
}

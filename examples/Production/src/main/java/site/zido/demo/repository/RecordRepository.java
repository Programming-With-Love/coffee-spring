package site.zido.demo.repository;

import site.zido.coffee.common.model.BaseRepository;
import site.zido.demo.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends BaseRepository<Record, Integer>, JpaRepository<Record, Integer> {
}

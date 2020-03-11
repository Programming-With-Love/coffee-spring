package site.zido.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.demo.entity.Room;

public interface RoomRepository extends JpaRepository<Room, String> {
}

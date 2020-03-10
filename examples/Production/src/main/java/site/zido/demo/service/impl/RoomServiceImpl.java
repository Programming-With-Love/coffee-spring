package site.zido.demo.service.impl;

import site.zido.demo.entity.Room;
import site.zido.demo.repository.RoomRepository;
import site.zido.demo.service.IRoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements IRoomService {
    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }
}

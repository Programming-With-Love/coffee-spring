package site.zido.demo.api;

import site.zido.demo.entity.Room;
import site.zido.demo.service.IRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private IRoomService roomService;

    public RoomController(IRoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> rooms() {
        return roomService.getRooms();
    }
}

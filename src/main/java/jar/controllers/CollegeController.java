package jar.controllers;

import jar.models.Building;
import jar.models.Room;
import jar.repositories.BuildingRepository;
import jar.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/college")

public class CollegeController {

    @Autowired
    private BuildingRepository buildingRepo;

    @Autowired
    private RoomRepository roomRepo;

    @GetMapping("/buildings")
    public List<Building> getBuildings() {
        return buildingRepo.findAll();
    }

    @GetMapping("/buildings/{id}/rooms")
    public List<Room> getRoomsByBuilding(@PathVariable Integer id) {
        return roomRepo.findByBuilding_BuildingId(id);
    }
}
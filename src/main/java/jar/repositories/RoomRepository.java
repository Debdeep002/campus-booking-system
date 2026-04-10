package jar.repositories;

import jar.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    // Custom method to fetch all 120 rooms when a specific building is clicked
    List<Room> findByBuilding_BuildingId(Integer buildingId);
}
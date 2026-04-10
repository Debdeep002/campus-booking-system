package jar.repositories;

import jar.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Custom method to check a specific room's existing bookings for a specific day
    List<Booking> findByRoom_RoomIdAndBookingDate(Integer roomId, LocalDate bookingDate);
    // Add this right below your other findBy method:
    List<Booking> findByStatus(String status);

    // 1. For the Student Grid (Only finds Approved slots)
    List<Booking> findByRoom_RoomIdAndBookingDateAndStatus(Integer roomId, LocalDate bookingDate, String status);

    // 2. For the new Admin Dashboard (Finds ALL requests for a specific room)
    List<Booking> findByRoom_RoomId(Integer roomId);

    // Find a user's request for a specific time to prevent duplicates
    List<Booking> findByUser_UserIdAndRoom_RoomIdAndBookingDateAndStartTime(Integer userId, Integer roomId, LocalDate bookingDate, LocalTime startTime);
    
    // Find all of a user's pending requests so we can paint them orange on the grid
    List<Booking> findByUser_UserIdAndRoom_RoomIdAndBookingDateAndStatus(Integer userId, Integer roomId, LocalDate bookingDate, String status);

    // 7. For the "My Bookings" page: Find all requests made by a specific user
    List<Booking> findByUser_UserId(Integer userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Booking b WHERE b.status IN :statuses AND b.bookingDate < :cutoffDate")
    void deleteOldRejectedBookings(List<String> statuses, java.time.LocalDate cutoffDate);
}
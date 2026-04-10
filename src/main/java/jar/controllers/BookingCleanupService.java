package jar.controllers; // Change this if you put it in a different folder!

import jar.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class BookingCleanupService {

    @Autowired
    private BookingRepository bookingRepository;

    // This Cron expression means: "Run at 00:00 (Midnight) every single day"
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpOldBookings() {
        
        // Calculate the date 7 days ago
        LocalDate cutoffDate = LocalDate.now().minusDays(7);
        
        // Define the statuses we want to delete
        List<String> statusesToDelete = Arrays.asList("Denied", "Denied_Seen", "Cancelled", "Cancelled_Seen");

        // Order the database to execute the deletion
        bookingRepository.deleteOldRejectedBookings(statusesToDelete, cutoffDate);
        
        System.out.println("🧹 Night Shift Janitor executed: Cleared old Denied/Cancelled bookings before " + cutoffDate);
    }
    
}
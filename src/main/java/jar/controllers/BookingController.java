package jar.controllers;

import jar.models.Booking;
import jar.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings") 
@CrossOrigin("*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private jar.repositories.RoomRepository roomRepository;

    // 1. Create a new booking (Now sends "Pending" from frontend)
    // 1. Create Booking (Now with spam protection and LocalTime compatibility!)
    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
            // NEW LOGIC: Block any dates that are today or in the past
            java.time.LocalDate today = java.time.LocalDate.now();
            if (!booking.getBookingDate().isAfter(today)) {
                return ResponseEntity.status(400).body("Error: Bookings must be made at least 1 day in advance.");
            }

            // NEW LOGIC: Fetch the full room details from the database
            jar.models.Room room = roomRepository.findById(booking.getRoom().getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            // --- NEW: RESOURCE-LEVEL ACCESS CONTROL ---
            String userRole = booking.getUser().getRole();
            String roomType = room.getRoomType();

            // 1. Staff Rooms: No one can book them
            if ("Staff Room".equalsIgnoreCase(roomType)) {
                return ResponseEntity.status(403).body("Error: Staff Rooms are strictly reserved and cannot be booked.");
            }

            // 2. Labs: Only Faculty can book them
            if ("Lab".equalsIgnoreCase(roomType) && "Student".equalsIgnoreCase(userRole)) {
                return ResponseEntity.status(403).body("Error: Security clearance required. Only Faculty members can book Laboratory rooms.");
            }

            // NEW LOGIC: Check if attendees exceed the room's capacity
            if (booking.getAttendeeCount() > room.getCapacity()) {
                // Returns a 400 Bad Request with a clear error message
                return ResponseEntity.status(400).body("Error: Your expected attendees (" + booking.getAttendeeCount() + 
                                                       ") exceeds the maximum capacity of " + room.getCapacity() + " for this room.");
            }
            // Check if this student already requested this exact slot
            // booking.getStartTime() automatically uses the LocalTime from your model!
            List<Booking> existing = bookingRepository.findByUser_UserIdAndRoom_RoomIdAndBookingDateAndStartTime(
                    booking.getUser().getUserId(), 
                    booking.getRoom().getRoomId(), 
                    booking.getBookingDate(), 
                    booking.getStartTime() 
            );

            // Loop through any existing requests to see if we should block this new one
            for (Booking b : existing) {
                if ("Pending".equals(b.getStatus())) {
                    return ResponseEntity.status(400).body("You already have a pending request for this time slot.");
                }
                if ("Approved".equals(b.getStatus()) || "Approved_Seen".equals(b.getStatus())) {
                    return ResponseEntity.status(400).body("You already have an approved booking for this time slot.");
                }
            }

            // If no duplicates are found, save it!
            bookingRepository.save(booking);
            return ResponseEntity.ok("Booking requested successfully!");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating booking: " + e.getMessage());
        }
    }

    // 2. Check room availability (For the Student grid)
    @GetMapping("/availability")
    public ResponseEntity<?> getBookingsForRoom(@RequestParam Integer roomId, @RequestParam String date, @RequestParam Integer userId) {
        try {
            LocalDate bookingDate = LocalDate.parse(date);
            List<Booking> approvedSlots = bookingRepository.findByRoom_RoomIdAndBookingDateAndStatus(roomId, bookingDate, "Approved");
            List<Booking> approvedSeenSlots = bookingRepository.findByRoom_RoomIdAndBookingDateAndStatus(roomId, bookingDate, "Approved_Seen");
            List<Booking> myPendingSlots = bookingRepository.findByUser_UserIdAndRoom_RoomIdAndBookingDateAndStatus(userId, roomId, bookingDate, "Pending");
            
            List<Booking> allSlotsToShow = new java.util.ArrayList<>();
            allSlotsToShow.addAll(approvedSlots);
            allSlotsToShow.addAll(approvedSeenSlots); // Added here!
            allSlotsToShow.addAll(myPendingSlots);
            
            return ResponseEntity.ok(allSlotsToShow);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching bookings: " + e.getMessage());
        }
    }

    // ==========================================
    //       NEW ADMIN WORKFLOW ENDPOINTS
    // ==========================================

    // 3. Get all pending requests for the Admin Dashboard
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingBookings() {
        try {
            // This relies on the new method we add to the Repository!
            return ResponseEntity.ok(bookingRepository.findByStatus("Pending"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching pending bookings: " + e.getMessage());
        }
    }

    // 4. Approve or Deny a specific booking
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Integer bookingId, @RequestParam String status) {
        try {
            // Find the booking by ID
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Change its status to Approved or Denied and save it
            booking.setStatus(status);
            bookingRepository.save(booking);
            
            // 3. THE NEW LOGIC: If we just Approved it, auto-deny the clashing ones!
            if ("Approved".equals(status)) {
                // Fetch all remaining "Pending" requests for this exact room and date
                List<Booking> overlappingRequests = bookingRepository.findByRoom_RoomIdAndBookingDateAndStatus(
                        booking.getRoom().getRoomId(), 
                        booking.getBookingDate(), 
                        "Pending"
                );

                // Loop through them
                for (Booking pendingReq : overlappingRequests) {
                    // If they are asking for the exact same start time, auto-deny them
                    if (pendingReq.getStartTime().equals(booking.getStartTime())) {
                        pendingReq.setStatus("Denied");
                        // Optional: You could make this "Auto-Denied" if you want the admin to know the system did it!
                        bookingRepository.save(pendingReq);
                    }
                }
            }
            
            return ResponseEntity.ok("Booking " + status + " successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating status: " + e.getMessage());
        }
    }

    // 2. NEW: Get all requests (Pending, Approved, Denied) for a specific room for the Admin
    @GetMapping("/room/{roomId}/all")
    public ResponseEntity<?> getAllBookingsForRoom(@PathVariable Integer roomId) {
        try {
            return ResponseEntity.ok(bookingRepository.findByRoom_RoomId(roomId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching room details: " + e.getMessage());
        }
    }

    // 5. NEW: Get all bookings for a specific user (For "My Bookings" page)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBookings(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(bookingRepository.findByUser_UserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user bookings: " + e.getMessage());
        }
    }
}
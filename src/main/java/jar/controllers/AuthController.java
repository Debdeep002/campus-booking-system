package jar.controllers;

import jar.models.User;
import jar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        Optional<User> user = userRepository.findByCollegeEmail(email);
        
        if (user.isPresent() && user.get().getPasswordHash().equals(password)) {
            // Create a JSON package to send to the browser
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "Login successful");
            responseData.put("userId", user.get().getUserId()); // <-- The Digital Nametag!
            responseData.put("role", user.get().getRole());
            responseData.put("fullName", user.get().getFullName()); 
            
            return ResponseEntity.ok(responseData);
        }
        
        // Return a 401 Unauthorized status if password fails
        return ResponseEntity.status(401).body("Invalid Credentials");
    }
}
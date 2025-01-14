package eu.sparfeld.gbtec.challenge.email;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // Create a new email
    @PostMapping
    public ResponseEntity<?> createEmail(@RequestBody CreateEmailDTO createEmailDTO) {
        try {
            EmailDTO savedEmail = emailService.createEmail(createEmailDTO);
            return ResponseEntity.ok(savedEmail);
        } catch (IllegalArgumentException e) {
            ProblemDetail problem = createProblemDetail(HttpStatus.BAD_REQUEST, "Invalid email data", e.getMessage());
            return ResponseEntity.badRequest().body(problem);
        }
    }

    // Get an email by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmail(@PathVariable Long id) {
        try {
            EmailDTO email = emailService.findById(id);
            return ResponseEntity.ok(email);
        } catch (IllegalArgumentException e) {
            ProblemDetail problem = createProblemDetail(HttpStatus.NOT_FOUND, "Email not found", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
        }
    }

    // Update an email by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmail(@PathVariable Long id, @RequestBody UpdateEmailDTO updateEmailDTO) {
        try {
            EmailDTO updatedEmail = emailService.updateEmail(id, updateEmailDTO);
            return ResponseEntity.ok(updatedEmail);
        } catch (IllegalStateException e) {
            ProblemDetail problem = createProblemDetail(HttpStatus.BAD_REQUEST, "Update conflict", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
        } catch (IllegalArgumentException e) {
            ProblemDetail problem = createProblemDetail(HttpStatus.NOT_FOUND, "Email not found", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
        }
    }


    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
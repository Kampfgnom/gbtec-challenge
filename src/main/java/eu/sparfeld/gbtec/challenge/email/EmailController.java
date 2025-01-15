package eu.sparfeld.gbtec.challenge.email;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<EmailsDTO> getEmails() {
        EmailsDTO emails = emailService.findAll();
        return ResponseEntity.ok(emails);
    }

    @PostMapping
    public ResponseEntity<EmailDTO> createEmail(@RequestBody CreateEmailDTO createEmailDTO) {
        EmailDTO savedEmail = emailService.createEmail(createEmailDTO);
        return ResponseEntity.ok(savedEmail);
    }

    @PostMapping("/bulk")
    public ResponseEntity<EmailsDTO> createEmails(@RequestBody CreateEmailsDTO createEmailsDTO) {
        EmailsDTO savedEmails = emailService.createEmails(createEmailsDTO);
        return ResponseEntity.ok(savedEmails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailDTO> getEmail(@PathVariable Long id) {
        EmailDTO email = emailService.findById(id);
        return ResponseEntity.ok(email);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailDTO> updateEmail(@PathVariable Long id, @RequestBody UpdateEmailDTO updateEmailDTO) {
        EmailDTO updatedEmail = emailService.updateEmail(id, updateEmailDTO);
        return ResponseEntity.ok(updatedEmail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> createProblemDetail(IllegalArgumentException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Not found");
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> createProblemDetail(IllegalStateException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid input data");
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> createProblemDetail(HttpMessageNotReadableException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid input data");
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
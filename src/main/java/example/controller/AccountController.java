package example.controller;

import example.model.dto.request.EmailRequest;
import example.model.dto.request.PassRequest;
import example.model.dto.request.PersonSettingsRequest;
import example.model.dto.request.RegisterRequest;
import example.model.dto.response.*;
import example.service.EmailService;
import example.service.PassService;
import example.service.PersonSettingsService;
import example.service.RegisterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "account", description = "Account interaction")
public class AccountController {

    private final RegisterService registerService;
    private final EmailService emailService;
    private final PassService passService;
    private final PersonSettingsService personSettingsService;
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request){
        return registerService.postRegister(request);
    }
    @PutMapping("/email/recovery")
    public RegisterResponse putRecoveryEmail(@RequestHeader("Authorization") String token){
        return emailService.putEmail(token);
    }
    @PutMapping("/email")
    public RegisterResponse putEmail(@RequestHeader("Authorization") String token, @RequestBody EmailRequest rq) {
        return emailService.recoverEmail(token, rq);
    }

    @PutMapping("/password/recovery")
    public RegisterResponse putEmailPassword(@RequestHeader("Authorization") String token) {
        return emailService.putPassword(token);
    }

    @PutMapping("/password/set")
    public RegisterResponse putPassword(@RequestHeader("Authorization") String token,
                                  @RequestBody PassRequest rq) {
        return passService.putPassword(token, rq.getPass());
    }

    @GetMapping("/notifications")
    public ListResponse<PersonSettingsResponse> getPersonSettings() {
        return personSettingsService.getPersonSettings();
    }

    @PutMapping("/notifications")
    public ResponseResponse<ComplexResponse> editPersonSettings(@RequestBody PersonSettingsRequest ps) {
        return personSettingsService.editPersonSettings(ps);
    }
}

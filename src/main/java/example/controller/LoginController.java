package example.controller;

import example.aop.InfoLogger;
import example.model.dto.request.LoginRequest;
import example.model.dto.response.CaptchaResponse;
import example.model.dto.response.ComplexResponse;
import example.model.dto.response.PersonResponse;
import example.model.dto.response.ResponseResponse;
import example.service.CaptchaService;
import example.service.LoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping
@InfoLogger
@Tag(name = "auth", description = "Authorization")
public class LoginController {
    private final LoginService loginService;
    private final CaptchaService captchaService;

    @PostMapping("login")
    public ResponseResponse<PersonResponse> login(@RequestBody LoginRequest loginRq) {
        return loginService.login(loginRq);
    }

    @PostMapping("logout")
    public ResponseResponse<ComplexResponse> logout() {
        return loginService.logout();
    }

    @GetMapping("captcha")
    public CaptchaResponse captcha() throws IOException {
        return captchaService.getCaptcha();
    }
}

package example.service;

import example.handler.exception.InvalidRequestException;
import example.model.dto.request.RegisterRequest;
import example.model.dto.response.ComplexResponse;
import example.model.dto.response.RegisterResponse;
import example.model.entity.Person;
import example.repository.CaptchaRepository;
import example.repository.PersonRepository;
import example.repository.PersonSettingsRepository;
import example.util.PhotoCloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonSettingsRepository personSettingsRepository;
    private final PhotoCloudinary photoCloudinary;
    private String captchaSecret1;
    private String captchaSecret2;
    private String password1;
    private String password2;
    @Value("${register-service.default-photo}")
    private String defaultPhoto;

    public RegisterResponse postRegister(RegisterRequest request) {

        captchaSecret1 = captchaRepository.findByCode(request.getCode()).getSecretCode();
        captchaSecret2 = request.getCodeSecret();
        password1 = request.getPass1();
        password2 = request.getPass2();


        if (!checkCaptcha()) {
            throw new InvalidRequestException("Wrong captcha entered.");
        }
        if (!checkPassword()) {
            throw new InvalidRequestException("Password mismatch.");
        }
        String email = request.getEmail();
        if (personRepository.checkEmailExists(email)) {
            throw new InvalidRequestException("This email is already registered.");
        }
        String firstName = request.getFirstName();
        String lastName = request.getLastName();


        Person person = new Person();
        person.setEmail(email);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setRegDate(System.currentTimeMillis());
        person.setPass(passwordEncoder.encode(password1));
        person.setPhoto(defaultPhoto);
        person.setIsApproved(true);
        person.setLastOnlineTime(System.currentTimeMillis());
        person.setIsDelete(false);
        var personId = personRepository.save(person);
        photoCloudinary.add(personId, defaultPhoto);
        personSettingsRepository.save(personId);

        var data = ComplexResponse.builder().message("ok").build();

        return RegisterResponse.builder().error("string")
                .timestamp(System.currentTimeMillis())
                .data(data).build();
    }

    private boolean checkPassword() {
        return password1.equals(password2);
    }

    private boolean checkCaptcha() {
        return captchaSecret1.equals(captchaSecret2);
    }
}

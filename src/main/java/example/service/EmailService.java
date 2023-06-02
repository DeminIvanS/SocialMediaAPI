package example.service;

import example.model.dto.request.EmailRequest;
import example.model.dto.response.ComplexResponse;
import example.model.dto.response.RegisterResponse;
import example.model.entity.Person;
import example.repository.PersonRepository;
import example.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;
    private final PersonRepository personRepository;
    @Value("${mailing-service.email}")
    private String fromEmail;
    @Value("${change-url.change-password}")
    private String changePassword;
    @Value("${change-url.change-email}")
    private String changeEmail;

    public RegisterResponse putPassword(String token) {


        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        String newToken = UUID.randomUUID().toString();
        person.setChangePassToken(newToken);
        personRepository.editPassToken(person);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Subject: Simple Mail");
        message.setText("Recovery Password: " + changePassword + newToken);

        mailSender.send(message);

        var data = ComplexResponse.builder().message("ok").build();

        return RegisterResponse.builder()
                .error("string")
                .data(data)
                .build();
    }

    public RegisterResponse putEmail(String token) {

        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        String newToken = UUID.randomUUID().toString();
        person.setChangePassToken(newToken);
        personRepository.editPassToken(person);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Subject: Simple Mail");
        message.setText("Recovery Link email: " + changeEmail + newToken);

        mailSender.send(message);

        var data = ComplexResponse.builder().message("ok").build();

        return RegisterResponse.builder()
                .error("string")
                .data(data)
                .build();
    }

    public RegisterResponse recoverEmail(String token, EmailRequest rq) {

        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));
        person.setEmail(rq.getEmail());
        personRepository.updateEmail(person);

        var data = ComplexResponse.builder().message("ok").build();

        return RegisterResponse.builder()
                .error("string")
                .data(data)
                .build();
    }
}

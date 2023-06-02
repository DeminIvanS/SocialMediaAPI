package example.service;

import example.model.dto.response.ComplexResponse;
import example.model.dto.response.RegisterResponse;
import example.model.entity.Person;
import example.repository.PersonRepository;
import example.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassService {
    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse putPassword(String token, String password) {

        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));
        person.setPass(passwordEncoder.encode(password));
        personRepository.editPass(person);

        var data = ComplexResponse.builder().message("ok").build();

        return RegisterResponse.builder()
                .error("string")
                .data(data)
                .build();
    }
}

package example.service;

import example.aop.DebugLogger;
import example.handler.exception.InvalidRequestException;
import example.model.dto.request.LoginRequest;
import example.model.dto.response.ComplexResponse;
import example.model.dto.response.PersonResponse;
import example.model.dto.response.ResponseResponse;
import example.model.entity.Person;
import example.repository.PersonRepository;
import example.security.jwt.JwtTokenProvider;
import example.util.PhotoCloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@DebugLogger
public class LoginService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhotoCloudinary photoCloudinary;

    public ResponseResponse<PersonResponse> profileResponse(String token) {
        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);

        PersonResponse personResponse = getPersonResponse(person, token);
        personResponse.setDeleted(person.getIsDelete());


        return new ResponseResponse<>("string", 0, 20, personResponse);
    }

    public ResponseResponse<PersonResponse> login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPass();
        Person person = personRepository.findByEmail(email);
        if (passwordEncoder.matches(password, person.getPass())) {
            String token = getToken(email);
            PersonResponse personResponse = getPersonResponse(person, token);
            ResponseResponse<PersonResponse> response = new ResponseResponse<>();
            response.setData(personResponse);
            response.setError("");
            response.setTimestamp(System.currentTimeMillis());
            return response;
        } else throw new InvalidRequestException("Invalid pass.");
    }

    private PersonResponse getPersonResponse(Person person, String token) {
        return PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(person.getRegDate())
                .birthDate(person.getBirthDate())
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(photoCloudinary.getUrl(person.getId()))
                .about(person.getAbout())
                .city(person.getCity())
                .country(person.getCountry())
                .messagePermission(person.getMessagePermission())
                .lastOnlineTime(person.getLastOnlineTime())
                .isBlocked(person.getIsBlocked())
                .token(token)
                .online(true)
                .build();

    }

    public ResponseResponse<ComplexResponse> logout() {
        var data = ComplexResponse.builder().message("ok").build();
        return new ResponseResponse<>("", data, null);
    }


    private String getToken(String email) {
        return jwtTokenProvider.createToken(email);
    }

}

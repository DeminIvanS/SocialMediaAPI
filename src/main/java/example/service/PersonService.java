package example.service;

import example.model.dto.request.UserRequest;
import example.model.dto.response.*;
import example.model.entity.Person;
import example.model.enums.FriendshipStatusCode;
import example.model.enums.MessagePermission;
import example.repository.FriendshipStatusRepository;
import example.repository.PersonRepository;
import example.security.jwt.JwtTokenProvider;
import example.security.jwt.JwtUser;
import example.util.PhotoCloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhotoCloudinary photoCloudinary;
    private final FriendshipStatusRepository friendshipStatusRepository;


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Person findById(int id) {
        return personRepository.findById(id);
    }

    public Person findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public ListResponse<PersonResponse> findPerson(String firstName, String lastName, Integer ageFrom, Integer ageTo,
                                                   String city, String country, int offset, int itemPerPage) {

        Person authorizedPerson = getAuthorizedPerson();
        List<Person> people = personRepository.findPerson(authorizedPerson, firstName, lastName,
                ageFrom, ageTo, city, country);
        return getResultJson(people, offset, itemPerPage);
    }

    public Person getAuthorizedPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        return personRepository.findByEmail(jwtUser.getUsername());
    }

    private ListResponse<PersonResponse> getResultJson(List<Person> people, int offset, int itemPerPage) {

        List<PersonResponse> data = people.stream()
                .map(person -> PersonResponse.builder()
                        .id(person.getId())
                        .firstName(person.getFirstName())
                        .lastName(person.getLastName())
                        .photo(photoCloudinary.getUrl(person.getId()))
                        .birthDate(person.getBirthDate())
                        .about(person.getAbout())
                        .phone(person.getPhone())
                        .lastOnlineTime(person.getLastOnlineTime())
                        .country(person.getCountry())
                        .city(person.getCity())
                        .friendshipStatusCode(getFriendshipStatus(person.getId()))
                        .online(Objects.equals(person.getOnlineStatus(), "ONLINE"))
                        .build())
                .collect(Collectors.toList());


        return new ListResponse<>("", offset, itemPerPage, data);
    }

    public PersonResponse getPersonRs(Person person) {

        return PersonResponse.builder()
                .id(person.getId())
                .email(person.getEmail())
                .phone(person.getPhone())
                .city(person.getCity())
                .country(person.getCountry())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(person.getRegDate())
                .birthDate(person.getBirthDate())
                .messagePermission(person.getMessagePermission())
                .isBlocked(person.getIsBlocked())
                .photo(photoCloudinary.getUrl(person.getId()))
                .about(person.getAbout())
                .lastOnlineTime(person.getLastOnlineTime())
                .friendshipStatusCode(getFriendshipStatus(person.getId()))
                .online(Objects.equals(person.getOnlineStatus(), "ONLINE"))
                .isDeleted(person.getIsDelete())
                .build();
    }

    public PersonResponse initialize(Integer personId) {

        Person person = findById(personId);
        return getPersonRs(person);
    }

    public UserResponse editUser(UserRequest request, String token) {

        UserResponse response = new UserResponse();
        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        String birthDate = request.getBirthDate().split("T")[0];
        LocalDate date = LocalDate.parse(birthDate, formatter);

        person.setBirthDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        person.setPhone(request.getPhone());
        person.setAbout(request.getAbout());
        person.setCity(request.getCity());
        person.setCountry(request.getCountry());
        person.setMessagePermission(request.getMessagePermission() == null ?
                MessagePermission.ALL : MessagePermission.valueOf(request.getMessagePermission()));
        personRepository.editPerson(person);

        return response;
    }

    public FriendshipStatusCode getFriendshipStatus(Integer dstId) {
        var srcId = getAuthorizedPerson().getId();
        var friendStatus = friendshipStatusRepository.findByPersonId(dstId, srcId);
        if (!friendStatus.isEmpty()) {
            return friendStatus.get(0).getCode();
        } else return FriendshipStatusCode.UNKNOWN;
    }

    public ResponseResponse<PersonResponse> getUserInfo(int userId) {

        return new ResponseResponse<>("", initialize(userId), null);
    }

    public Person getPersonByToken(String token) {
        String email = jwtTokenProvider.getUsername(token);
        return personRepository.findByEmail(email);
    }


    public ResponseResponse<ComplexResponse> deleteUser(String token) {
        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        person.setIsDelete(true);
        personRepository.setPersonIsDelete(person);
        ResponseResponse response = new ResponseResponse<>();
        response.setData(ComplexResponse.builder().message("ok").build());
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    //    @Scheduled(fixedDelayString = "PT24H")
//    @Async
    public void fullDeleteUser(Person person) {
        personRepository.findAll().stream().filter(Person::getIsBlocked)
                .filter(person1 -> {
                    LocalDate deletedDate = Instant.ofEpochMilli(person1.getDeletedTime())
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    return deletedDate.isBefore(deletedDate.plusMonths(1));
                }).forEach(personRepository::fullDeletePerson);
    }

    public ResponseResponse<ComplexResponse> recoverUser(String token) {

        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        person.setIsDelete(false);
        personRepository.setPersonIsDelete(person);

        ResponseResponse response = new ResponseResponse<>();
        response.setData(ComplexResponse.builder().message("ok").build());
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}

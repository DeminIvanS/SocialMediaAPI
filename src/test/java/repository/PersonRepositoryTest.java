package repository;

import example.model.entity.Person;
import example.model.enums.MessagePermission;
import example.repository.PersonRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class PersonRepositoryTest {
    @Autowired
    private PersonRepository personRepository;

    @Test
    void save_shouldSavePerson_whenFieldsCorrectAndNotExists() {
        int preSaveCount = 0;
        int expectedCount = 1;
        Person person = getPerson();

        personRepository.save(person);
        int postSaveCount = personRepository.getCount();
        int actualCount = postSaveCount - preSaveCount;

        assertEquals(expectedCount, actualCount);
    }

    private Person getPerson() {
        Person person = new Person();

        person.setId(1);
        person.setFirstName("Alex");
        person.setLastName("Fred");
        person.setRegDate(Timestamp.valueOf(LocalDateTime.of(2007, 7, 12, 12, 12)).getTime());
        person.setBirthDate(Timestamp.valueOf(LocalDateTime.of(2010, 7, 12, 12, 12)).getTime());
        person.setEmail("qwerty@mail.ru");
        person.setPhone("89999999999");
        person.setPass("123456");
        person.setPhoto("http://www.photo.com");
        person.setAbout("about");
        person.setCity(null);
        person.setConfirmCode(123456);
        person.setIsApproved(true);
        person.setMessagePermission(MessagePermission.ALL);
        person.setLastOnlineTime(Timestamp.valueOf(LocalDateTime.of(2012, 7, 12, 12, 12)).getTime());
        person.setIsBlocked(false);

        return person;
    }
}

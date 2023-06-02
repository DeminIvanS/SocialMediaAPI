package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.handler.exception.ErrorException;
import example.handler.exception.InvalidRequestException;
import example.mapper.PersonMapper;
import example.model.entity.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonRepository {

    public static final String PERSON_ID = "person id = ";
    private final RowMapper<Person> rowMapper = new PersonMapper();
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipRepository friendshipRepository;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final PersonSettingsRepository personSettingsRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public Integer save(Person person) {
        String sqlQuery = "INSERT INTO person(first_name, last_name, reg_date, email, pass, " +
                "photo, is_approved, last_online_time, is_delete" +
                "VALUE ('%s', '%s', '%s', '%s', '%s', '%s', '%d', '%s', '%s')";
        String sqlFormat = String.format(sqlQuery,
                person.getFirstName(),
                person.getLastName(),
                new Timestamp(person.getRegDate()),
                person.getEmail(),
                person.getPass(),
                person.getPhoto(),
                person.getIsApproved(),
                new Timestamp(person.getLastOnlineTime()),
                person.getIsDelete());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(sqlFormat, new String[]{"id"}), keyHolder);
        return (Integer) keyHolder.getKey();
    }

    public Person findById(int id) {
        try {
            String sqlQuery = "SELECT * FROM person WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public Person findNotDeletedById(int id) {
        try {
            String sqlQuery = "SELECT * FROM person WHERE is_delete = false AND id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public List<Person> getFriendsPersonById(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM friendship fs\n" +
                    "JOIN friendship_status fss ON fss.id\n" +
                    "JOIN person p ON fs.dst_person_id = p.id\n" +
                    "WHERE fss.code = 'FRIEND' AND src_person_id = ?";
            return jdbcTemplate.query(sqlQuery, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public Person findByEmail(String email) {
        try {
            String sqlQuery = "SELECT * FROM person WHERE email LIKE ?";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidRequestException("User with mail - " + email + " not found.");
        }
    }

    public Integer getCount() {
        String sqlQuery = "SELECT COUNT(*) FROM person";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
    }

    public Person getPersonById(Integer id) {
        String sqlQuery = "SELECT * FROM person WHERE id = ";
        return jdbcTemplate.queryForObject(sqlQuery + id, Person.class);
    }

    private List<Person> filterBlockPerson(List<Person> personList, int authPersonId) {
        List<Person> blockedPerson = new ArrayList<>();
        String sqlQuery = "SELECT * FROM person AS p JOIN c AS fs ON p.id = fs.dst_person_id\n" +
                "JOIN friendship_status AS fss ON fs.status_id = fss.id WHERE fs.src_person_id = " +
                authPersonId + " AND fs.dst_person_id = ? AND fss.code = 'BLOCKED';";
        personList.stream().map(person -> jdbcTemplate.query(sqlQuery, rowMapper, person.getId()))
                .forEach(blockedPerson::addAll);
        personList.removeAll((blockedPerson));
        return personList;

    }

    public List<Person> findPerson(Person authPerson, String firstName, String lastName,
                                   Integer ageFrom, Integer ageTo, String city, String country) {
        List<String> query = new ArrayList<>();
        if (firstName != null) {
            query.add("first_name = '" + firstName + "'");
        }
        if (lastName != null) {
            query.add("last_name = '" + lastName + "'");
        }
        if (ageFrom != null) {
            query.add("date_part('year', age(birth_date))::int > " + ageFrom);
        }
        if (ageTo != null) {
            query.add("date_part('year', age(birth_date))::int < " + ageTo);
        }
        if (city != null) {
            query.add("city = '" + city + "'");
        }
        if (country != null) {
            query.add("country = '" + country + "'");
        }
        String sqlQuery = "SELECT * FROM person WHERE id != " + authPerson.getId() +
                " AND " + String.join(" AND ", query) + ";";
        return filterBlockPerson(jdbcTemplate.query(sqlQuery, rowMapper), authPerson.getId());
    }

    public List<Person> getFriendsPersonId(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM person p\n" +
                    "JOIN friendship fs ON fs.dst_person_id = p.id\n" +
                    "JOIN friendship_status fss ON fss.id = fs.status_id\n" +
                    "WHERE fss.code = 'FRIEND' AND id_delete is false " +
                    "AND src_person_id = ? OR dst_person_id = ?";
            return jdbcTemplate.query(sqlQuery, rowMapper, id, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public List<Person> getApplicationsFriendsPersonById(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM person p \n" +
                    "JOIN friendship fs ON fs.src_person_id = p.id\n" +
                    "JOIN friendship_status fss ON fss.id = fs.status_id\n" +
                    "WHERE fss.code = 'REQUEST' AND dst_person_id = ? AND id_delete is false";
            return jdbcTemplate.query(sqlQuery, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public Boolean editPerson(Person person) {
        boolean repeatValue;
        String sqlQuery = "UPDATE person SET first_name = ?, last_name = ?, " +
                "birth_date = ?, phone = ?, about = ?, city = ?, country = ?, message_permission = ? " +
                "WHERE id = ?";
        try {
            repeatValue = jdbcTemplate.update(sqlQuery, person.getFirstName(), person.getLastName(),
                    new Timestamp(person.getBirthDate()), person.getPhone(), person.getAbout(), person.getCity(),
                    person.getCountry(), person.getMessagePermission().toString(), person.getId()) == 1;
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        return repeatValue;
    }

    public Boolean editPass(Person person) {

        String sqlQuery = "UPDATE person SET pass = ? WHERE id = ?";
        try {
            return jdbcTemplate.update(sqlQuery, person.getPass(), person.getId()) == 1;
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
    }
    public void savePhoto(Person person) {
        try {
            jdbcTemplate.update("UPDATE person SET photo = ? WHERE id = ?", person.getPhoto(),
                    person.getId());
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }
    public void updateEmail(Person person) {
        String sqlQuery = "UPDATE person SET email = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sqlQuery, person.getEmail(), person.getId());
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
    }
    public void editPassToken(Person person) {
        String sqlQuery = "UPDATE person SET change_pass_token = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sqlQuery, person.getChangePassToken(), person.getId());
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
    }
    public List<Person> getByBirthday(String birthday) {
        String sqlQuery = "SELECT * FROM person WHERE birth_date = '" + birthday + "'";
        try {
            return  jdbcTemplate.query(sqlQuery, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("birt_date = " + birthday);
        }
    }
    public List<Person> findAll () {
        return jdbcTemplate.query("SELECT * FROM person", rowMapper);
    }
    public Boolean deletePerson(int id){
        try {
            return jdbcTemplate.update("DELETE * FROM person WHERE id = ?", id) == 1;
        }catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
    }
    public Boolean fullDeletePerson(Person person){
        try {
            dialogRepository.findByPersonId(person.getId()).forEach(dialog -> {
                messageRepository.deleteByDialogId(dialog.getId());
                dialogRepository.deleteById(dialog.getId());
            });
            friendshipRepository.findByPersonId(person.getId()).forEach(friendshipRepository::delete);
            personSettingsRepository.delete(person.getId());
            postRepository.findAllUserPosts(person.getId()).forEach(post-> {
                tagRepository.deleteTagsByPostId(post.getId());
                postRepository.finalDeletePostById(post.getId());
            });
            return jdbcTemplate.update("DELETE FROM person WHERE id = ?", person.getId()) == 1;
        }catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
    }

    public void updateNotificationSessionId(Person person) {
        String sqlQuery = "UPDATE person SET notification_session_id = ? " +
                "online_status = ?, last_online_time = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, person.getNotificationSessionId(), person.getOnlineStatus(),
                new Timestamp(person.getLastOnlineTime()), person.getIsDelete());
    }
    public List<Person> findBySessionId(String sessionId) {
        String sqlQuery = "UPDATE person SET notification_session_id = ?";
        return jdbcTemplate.query(sqlQuery,rowMapper,sessionId);
    }
    public void deleteSessionId(Person person){
        String sqlQuery = "UPDATE person SET notification_session_id = null, online_status = 'OFFLINE', " +
                "last_online_time = ?, WHERE id = ?";
        jdbcTemplate.update(sqlQuery, new Timestamp(person.getLastOnlineTime()), person.getId());
    }
    public boolean checkEmailExists(String email) {
        String sqlQuery = "SELECT * FROM person WHERE email = ?";
        var rs = jdbcTemplate.query(sqlQuery,rowMapper,email);
        return !rs.isEmpty();
    }
    public boolean setPersonIsDelete(Person person) {
        try {
            return jdbcTemplate.update("UPDATE person SET is_delete = ?, " +
                    "delete_time = ?, WHERE id = ?", person.getIsDelete(), LocalDateTime.now(), person.getId()) == 1;
        }catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
    }
}
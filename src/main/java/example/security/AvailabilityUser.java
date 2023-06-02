package example.security;

import example.model.entity.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RequiredArgsConstructor

public class AvailabilityUser {
    private final JdbcTemplate jdbcTemplate;;
    private static final String ID = "id";
    private static final String EMAIL = "email";

    public boolean checkUser(String user){
        List<Person> allPerson = findAllPerson();
        for(Person p : allPerson){
            String emailNotSpace = p.getEmail().replaceAll("\\s+","");
            if(emailNotSpace.equals(user)){
                return true;
            }
        }
        return false;
    }
    public List<Person> findAllPerson(){
        String sql = "select * from person";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Person person = new Person();
                    person.setId(rs.getInt(ID));
                    person.setEmail(rs.getString(EMAIL));
                    return person;
                }
        );
    }
}

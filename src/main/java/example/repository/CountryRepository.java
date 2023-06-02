package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.mapper.CountryMapper;
import example.model.entity.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CountryRepository {
    private final RowMapper<Country> rowMapper = new CountryMapper();
    private final JdbcTemplate jdbcTemplate;

    public Country findById(int id) {
        try {
            String sqlQuery = "SELECT * FROM country WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        }catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("country id = " + id);
        }
    }
}

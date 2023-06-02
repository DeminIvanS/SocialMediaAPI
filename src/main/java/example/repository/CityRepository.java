package example.repository;

import example.mapper.CityMapper;
import example.model.entity.City;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CityRepository {

    private final RowMapper<City> rowMapper = new CityMapper();
    private final JdbcTemplate jdbcTemplate;

    public List<City> findByTitle(String city) {

        String sql = "SELECT * FROM city WHERE title = ?";
        return jdbcTemplate.query(sql, rowMapper);
    }
    public void saveOrUpdate(City city) {
        String sql = "INSERT INTO city (title, country)id, temp, cloud) VALUE (?,?,?,?) " +
                "ON conflict (title) DO update SET temp = ?, clouds = ?";
        jdbcTemplate.update(sql, city.getTitle(), city.getCountryId(), city.getTemp(), city.getClouds());
    }
}

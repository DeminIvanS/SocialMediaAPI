package example.mapper;

import example.model.entity.City;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CityMapper implements RowMapper<City> {
    @Override
    public City mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        City city = new City();

        city.setId(resultSet.getInt("id"));
        city.setTitle(resultSet.getString("title"));
        city.setCountryId(resultSet.getInt("country_id"));
        city.setClouds(resultSet.getString("clouds"));
        city.setTemp(resultSet.getString("temp"));

        return city;
    }

}

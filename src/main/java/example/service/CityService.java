package example.service;

import example.model.entity.City;
import example.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    public List<City> findByTitle(String city) {
        return cityRepository.findByTitle(city);
    }
}

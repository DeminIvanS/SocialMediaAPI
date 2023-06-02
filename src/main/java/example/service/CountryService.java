package example.service;

import example.model.entity.Country;
import example.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;


    public Country findById(int id) {
        return countryRepository.findById(id);
    }
}

package example.model.dto.response;

import example.model.entity.Country;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountryResponse {

    private int id;
    private String title;

    public CountryResponse(Country country){
        this.id = country.getId();
        this.title = country.getTitle();;
    }
}

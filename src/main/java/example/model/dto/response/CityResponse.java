package example.model.dto.response;

import example.model.entity.City;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityResponse {

    private int id;
    private String title;

    public CityResponse(City city){
        id = city.getId();
        title = city.getTitle();
    }

}

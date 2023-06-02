package example.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {

    private int id;
    private String title;
    private int countryId;
    private String temp;
    private String clouds;
}

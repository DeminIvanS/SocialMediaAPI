package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ListResponse<T> {
    private String error;
    private Long timestamp;
    private Integer total;
    private Integer offset;
    @JsonProperty("item_per_page")
    private Integer perPage;
    private List<T> data;
    @JsonProperty("error_description")
    private String errorDescription;

    public ListResponse(String error, Integer offset, Integer perPage, List<T> data) {
        this.error = error;
        this.timestamp = System.currentTimeMillis();
        this.total = data.size();
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }
}

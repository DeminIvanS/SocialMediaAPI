package example.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeRequest {
    @JsonProperty("item_id")
    private  Integer itemId;
    private String type;
}

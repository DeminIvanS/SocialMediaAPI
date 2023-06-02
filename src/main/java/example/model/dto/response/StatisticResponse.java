package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class StatisticResponse {
    @JsonProperty("overall")
    private OverallStatisticResponse overallStatisticResponse;
    @JsonProperty("personal")
    private PersonalStatisticResponse personalStatisticResponse;
}

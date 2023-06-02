package example.model.dto.response;

import lombok.Data;

@Data
public class StorageResponse {
    private String error;
    private long timestamp;
    private StorageDataResponse data;
}

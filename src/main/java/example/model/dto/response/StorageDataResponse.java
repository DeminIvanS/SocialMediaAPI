package example.model.dto.response;

import lombok.Data;

@Data
public class StorageDataResponse {
    private String id;
    private int ownerId;
    private String fileName;
    private String relativeFilePath;
    private String fileFormat;
    private int bytes;
    private String fileType;
    private int createdAt;

}

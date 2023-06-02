package example.controller;

import com.dropbox.core.DbxException;
import example.model.dto.response.StorageResponse;
import example.service.StorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Tag(name = "storage", description = "Storage interaction")
public class StorageController {
    private final StorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StorageResponse postStorage(@RequestHeader("Authorization") String token,
                                       @RequestParam MultipartFile file) throws DbxException {
        return storageService.postStorage(file, token);
    }
}

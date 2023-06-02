package example.service;

import example.model.dto.response.StorageDataResponse;
import example.model.dto.response.StorageResponse;
import example.model.entity.Person;
import example.repository.PersonRepository;
import example.security.jwt.JwtTokenProvider;
import example.util.DropBox;
import example.util.PhotoCloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhotoCloudinary photoCloudinary;

    public StorageResponse postStorage(MultipartFile image, String token) {
        String imageName = null;

        StorageResponse response = new StorageResponse();
        response.setError("string");
        response.setTimestamp(System.currentTimeMillis());

        if (image == null) {
            return response;
        }
        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));

        String photo = DropBox.dropBoxUploadImages(image);
        person.setPhoto(photo);
        photoCloudinary.add(person.getId(), photo);
        StorageDataResponse storageDataRs = new StorageDataResponse();
        storageDataRs.setId(imageName);
        storageDataRs.setOwnerId(person.getId());
        storageDataRs.setFileName(image.getName());
        storageDataRs.setFileFormat(image.getContentType());
        personRepository.savePhoto(person);

        return response;
    }
}

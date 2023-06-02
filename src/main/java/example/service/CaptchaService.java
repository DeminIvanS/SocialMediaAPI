package example.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import example.model.dto.response.CaptchaResponse;
import example.repository.CaptchaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CaptchaService {
    private final CaptchaRepository captchaRepository;
    private static final String CAPTCHA = "captcha.jpg";
    public CaptchaResponse getCaptcha() throws IOException{
        StringBuilder image = new StringBuilder("data:image/png;base64, ");
        CaptchaResponse response = new CaptchaResponse();
        Cage cage = new GCage();
        String captchaCode = cage.getTokenGenerator().next();
        String secretCode = cage.getTokenGenerator().next();

        OutputStream stream = new FileOutputStream(CAPTCHA, false);
        cage.draw(secretCode, stream);
        stream.flush();
        stream.close();

        byte[] captchaByte = Files.readAllBytes(Paths.get(CAPTCHA));
        Files.delete(Paths.get(CAPTCHA));
        String encodedCaptcha = DatatypeConverter.printBase64Binary(captchaByte);
        image.append(encodedCaptcha);

        captchaRepository.addCaptcha(System.currentTimeMillis(), captchaCode, secretCode);
        response.setImage(image.toString());
        response.setCode(captchaCode);
        return response;

    }
    @Async
    @Scheduled(fixedDelayString = "PT1H")
    public void deleteCaptcha(){
        captchaRepository.findAll().stream()
                .filter(captcha -> captcha.getTime().isBefore(LocalDateTime.now().minusMinutes(15)))
                        .forEach(captchaRepository::deleteCaptcha);

    }
}

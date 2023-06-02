package example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import example.model.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Notification> kafkaTemplate;

    public void sendNotificationToQueue(Notification notification) throws JsonProcessingException {

        kafkaTemplate.send("notifications", notification);
    }
}

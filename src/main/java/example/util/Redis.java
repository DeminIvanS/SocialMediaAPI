package example.util;

import example.config.RedisConfig;
import example.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Redis {

    private RedissonClient redisson;

    private RMap<String, String> usersPhoto;
    private final String name = "USER_PHOTO";
    private final PersonRepository personRepository;
    private final DropBox dropBox;
    private final RedisConfig redisConfig;

    private void init() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisConfig.getUrl());
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            System.out.println("Не удалось подключиться к Redis");
            System.out.println(Exc.getMessage());
        }
        usersPhoto = redisson.getMap(name);
    }

    public void add(Integer id, String url) {
        String currentUrl = dropBox.getLinkImage(url);
        if (usersPhoto.containsKey(String.valueOf(id))) {
            usersPhoto.fastRemove(String.valueOf(id));
        }
        usersPhoto.fastPut(String.valueOf(id), currentUrl);
    }

    public String getUrl(Integer id) {
        return usersPhoto.get(String.valueOf(id));
    }

    @Scheduled(initialDelay = 6000, fixedDelayString = "PT24H")
    @Async
    private void updateUrl() {
        if (redisson == null) {
            init();
        }
        personRepository.findAll().forEach(person ->
                add(person.getId(), person.getPhoto()));
    }

    public void shutdown() {
        redisson.shutdown();
    }
}

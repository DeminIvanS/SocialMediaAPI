package example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class YandexMailConfig {
    @Value("spring.mail.host")
    private String host;
    @Value("spring.mail.username")
    private String username;
    @Value("spring.mail.pass")
    private String pass;
    @Value("spring.mail.port")
    private String port;
    @Value("spring.mail.protocol")
    private String protocol;
    @Value("spring.mail.debug")
    private String debug;

    public JavaMailSender getMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(465);
        mailSender.setUsername(username);
        mailSender.setPassword(pass);

        Properties properties = mailSender.getJavaMailProperties();

        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.debug", debug);

        return mailSender;
    }
}

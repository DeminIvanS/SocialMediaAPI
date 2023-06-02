package example.service;

import example.security.jwt.JwtTokenProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private static final String TOKEN_HEADER = "token";
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel){

        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;
        if(accessor.getCommand() == StompCommand.CONNECT){
            final String token = accessor.getFirstNativeHeader(TOKEN_HEADER);
            accessor.setUser(jwtTokenProvider.getAuthentication(token));
        }
        return message;
    }
}

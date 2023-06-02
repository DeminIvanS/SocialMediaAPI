package example.security;

import example.security.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.Authenticator;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    private final JwtTokenFilter jwtTokenFilter;

    private static final String MAIN_ENDPOINT = "/";
    private static final String STATIC_ENDPOINT = "/static/**";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String REGISTER_ENDPOINT = "/api/v1/account/register";
    private static final String PASS_RECOVERY_ENDPOINT = "/api/v1/account/pass/recovery";
    private static final String CAPTCHA_ENDPOINT = "/api/v1/auth/captcha";
    private static final String GRAFANA_ENDPOINT = "/actuator/**";
    private static final String WEBSOCKET_ENDPOINT = "/api/v1/ws/**";
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .cors()
                .configurationSource(configurationSource())
        .and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(MAIN_ENDPOINT,STATIC_ENDPOINT,LOGIN_ENDPOINT,REGISTER_ENDPOINT,
                        PASS_RECOVERY_ENDPOINT,CAPTCHA_ENDPOINT,GRAFANA_ENDPOINT,WEBSOCKET_ENDPOINT,
                        "/swagger-ui.html", "/swagger-ui/index.html", "/swagger-ui/**","/v3/api-docs/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
    CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.setAllowedOrigins(List.of("http://localhost:8080","http://localhost:8086",
                "http://localhost:8081","http://localhost:80"));
        configuration.setAllowedMethods(List.of("OPTIONS", "DELETE", "POST", "GET", "PATCH", "PUT"));
        configuration.setExposedHeaders(List.of("Content-Type", "X-Request-With", "accept", "Origin",
        "Access-Control-Request-Method","Access-Control-Request-Headers", "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

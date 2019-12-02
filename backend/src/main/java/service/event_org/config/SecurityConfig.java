package service.event_org.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import service.event_org.service.UserService;
import service.event_org.utils.Constants;
import service.event_org.utils.Utils;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:35
 * Purpose: Authorization and Authentication by spring security.
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final DataSource dataSource;
    @Value("${cors_url}")
    private String corsURL;

    @Autowired
    public SecurityConfig(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    /**
     * Configure spring security settings for the url accesses.
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Enable cors, disable csrf and handle authentication failure
        http.cors().and().
                csrf().disable().
                exceptionHandling().authenticationEntryPoint(
                (request, response, authException) ->
                        Utils.createJSONErrorResponse(HttpServletResponse.SC_UNAUTHORIZED,
                                Constants.ErrorMsg.UNAUTHORIZED, response)
        ).accessDeniedHandler( //Handles forbidden access
                ((request, response, accessDeniedException) ->
                        Utils.createJSONErrorResponse(HttpServletResponse.SC_FORBIDDEN,
                                Constants.ErrorMsg.FORBIDDEN_RESOURCE, response))
        ).and().
                authorizeRequests().antMatchers("/api/v1/events/**").authenticated(). //Authenticate all requests for events.
                and().
                logout().permitAll().logoutSuccessHandler( //Add logout functionality given by spring security
                (request, response, authentication) -> new HttpStatusReturningLogoutSuccessHandler()
        );
    }

    /**
     * Enables jdbc authentication
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(encoder()).and().jdbcAuthentication().
                dataSource(dataSource);
    }

    /**
     * Hashing for passwords.
     *
     * @return
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration to enable CORS
     *
     * @return
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Collections.singletonList(corsURL));
        configuration.setAllowedMethods(Arrays.asList("POST", "PUT", "GET", "OPTIONS", "DELETE", "PATCH"));
        configuration.setMaxAge(3600L);
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "WWW-Authenticate", "Authorization", "Origin",
                "Content-Type", "Version"));
        configuration.setExposedHeaders(Arrays.asList("X-Requested-With", "WWW-Authenticate",
                "Authorization", "Origin", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

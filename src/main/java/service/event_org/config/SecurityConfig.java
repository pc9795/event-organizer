package service.event_org.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.session.SessionManagementFilter;
import service.event_org.beans.CORSFilter;
import service.event_org.service.UserService;
import service.event_org.utils.Constants;
import service.event_org.utils.Utils;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:35
 * Purpose: Authorization and Authentication by spring security.
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final DataSource dataSource;
    private final CORSFilter corsFilter;

    @Autowired
    public SecurityConfig(DataSource dataSource, UserService userService, CORSFilter corsFilter) {
        this.dataSource = dataSource;
        this.userService = userService;
        this.corsFilter = corsFilter;
    }

    /**
     * Configure spring security settings for the url accesses.
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Disable csrf and handle authentication failure
        http.addFilterBefore(corsFilter, SessionManagementFilter.class).csrf().disable().exceptionHandling().
                authenticationEntryPoint(
                        (request, response, authException) ->
                                Utils.createJSONErrorResponse(HttpServletResponse.SC_UNAUTHORIZED,
                                        Constants.ErrorMsg.UNAUTHORIZED, response)
                ).accessDeniedHandler( //Handles forbidden access
                ((request, response, accessDeniedException) ->
                        Utils.createJSONErrorResponse(HttpServletResponse.SC_FORBIDDEN,
                                Constants.ErrorMsg.FORBIDDEN_RESOURCE, response))
        ).and().authorizeRequests().antMatchers("/api/v1/events/**").authenticated(). //Authenticate all requests for events.
                and().logout().permitAll().logoutSuccessHandler( //Add logout functionality given by spring security
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
}

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
import service.event_org.service.UserService;
import service.event_org.utils.Constants;
import service.event_org.utils.Utils;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:35
 * Purpose: TODO:
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().exceptionHandling().authenticationEntryPoint(
                (request, response, authException) ->
                        Utils.createJSONErrorResponse(HttpServletResponse.SC_UNAUTHORIZED,
                                Constants.ErrorMsg.UNAUTHORIZED, response)

        ).accessDeniedHandler(
                ((request, response, accessDeniedException) ->
                        Utils.createJSONErrorResponse(HttpServletResponse.SC_FORBIDDEN,
                                Constants.ErrorMsg.FORBIDDEN_RESOURCE, response))
        ).and().authorizeRequests().antMatchers("/api/v1/events/**").authenticated().
                and().logout().permitAll().logoutSuccessHandler(
                (request, response, authentication) -> new HttpStatusReturningLogoutSuccessHandler()
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(encoder()).and().jdbcAuthentication().
                dataSource(dataSource);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}

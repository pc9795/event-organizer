package service.event_org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:33
 * Purpose: TODO:
 **/
@EnableSwagger2
@EnableWebSecurity
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

package eu.rudisch.authorizationadmin.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan({ "eu.rudisch.web.controller" })
public class AuthorizationAdminWebConfig implements WebMvcConfigurer {

}

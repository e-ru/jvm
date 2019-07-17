package eu.rudisch.authorizationserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

@Configuration
@PropertySource("classpath:custom.yml")
@ConfigurationProperties
@Getter
@Setter
public class CustomProperties {
	private boolean reuseRefreshtoken;
	private String keyStorePath;
	private String keyStoreAlias;
	private String keyStorePassword;
}

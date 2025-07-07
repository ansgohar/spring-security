package me.agohar.usersec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import me.agohar.usersec.security.config.GcpProperties;

@SpringBootApplication
@EnableConfigurationProperties(GcpProperties.class)
public class UsersecApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersecApplication.class, args);
	}

}

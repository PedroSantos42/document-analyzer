package com.pedrosantos.challenge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.pedrosantos.challenge.config.StorageProperties;
import com.pedrosantos.challenge.providers.DiskStorageProvider;

@SpringBootApplication
@ComponentScan({ "com.pedrosantos.challenge" })
@EntityScan({ "com.pedrosantos.challenge" })
@EnableJpaRepositories({ "com.pedrosantos.challenge" })
@EnableConfigurationProperties(StorageProperties.class)
public class ChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}

	@Bean
	CommandLineRunner init(DiskStorageProvider diskStorage) {
		return (args) -> {
			diskStorage.deleteAll();
			diskStorage.init();
		};
	}
}

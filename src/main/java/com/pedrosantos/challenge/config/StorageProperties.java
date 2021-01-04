package com.pedrosantos.challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@ConfigurationProperties("storage")
@Getter
public class StorageProperties {

	@Value("${storage.folder}")
	private String location;
}

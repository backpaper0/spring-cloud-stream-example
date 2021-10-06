package com.example;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

@SpringBootApplication
@RestController
public class SourceApp {

	public static void main(final String[] args) {
		SpringApplication.run(SourceApp.class, args);
	}

	private static Logger logger = LoggerFactory.getLogger(SourceApp.class);

	private final StreamBridge streamBridge;

	public SourceApp(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void handle(@RequestBody final Person person) {
		if (logger.isInfoEnabled()) {
			logger.info("Source -> MQ: {}", person);
		}
		streamBridge.send("person", person);
	}

	@PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
	public void handle(@RequestBody String text) {
		if (logger.isInfoEnabled()) {
			logger.info("Source -> MQ: {}", text);
		}
		streamBridge.send("person", text);
	}

	public static class Person {

		private final String name;

		public Person(@JsonProperty("name") final String name) {
			this.name = Objects.requireNonNull(name);
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}

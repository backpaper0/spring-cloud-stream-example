package com.example;

import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.handler.LoggingHandler;

import com.fasterxml.jackson.annotation.JsonProperty;

@SpringBootApplication
public class SinkApp {

	private static Logger logger = LoggerFactory.getLogger(SinkApp.class);

	public static void main(final String[] args) {
		SpringApplication.run(SinkApp.class, args);
	}

	@Bean
	public Consumer<Person> person() {
		return person -> {
			if (logger.isInfoEnabled()) {
				logger.info("MQ -> Sink: {}", person);
			}
		};
	}

	public static class Person {

		private final String name;

		public Person(@JsonProperty("name") String name) {
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

	@Autowired
	public void configureLoggingHandler(LoggingHandler handler) {
		logger.info("Set {} log level to {}", handler, LoggingHandler.Level.WARN);
		handler.setLevel(LoggingHandler.Level.WARN);
	}
}

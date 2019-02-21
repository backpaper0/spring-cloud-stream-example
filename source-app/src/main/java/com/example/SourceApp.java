package com.example;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

@SpringBootApplication
@EnableBinding(Source.class)
@RestController
public class SourceApp {

    private static Logger logger = LoggerFactory.getLogger(SourceApp.class);

    private final Source source;

    public static void main(final String[] args) {
        SpringApplication.run(SourceApp.class, args);
    }

    public SourceApp(final Source source) {
        this.source = Objects.requireNonNull(source);
    }

    @PostMapping
    public void handle(@RequestBody final Person person) {
        if (logger.isInfoEnabled()) {
            logger.info("Source -> MQ: {}", person);
        }
        final Message<?> message = MessageBuilder.withPayload(person).build();
        source.output().send(message);
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

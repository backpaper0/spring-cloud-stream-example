package com.example;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import com.fasterxml.jackson.annotation.JsonProperty;

@SpringBootApplication
@EnableBinding(Sink.class)
public class SinkApp {

    private static Logger logger = LoggerFactory.getLogger(SinkApp.class);

    public static void main(final String[] args) {
        SpringApplication.run(SinkApp.class, args);
    }

    @StreamListener(Sink.INPUT)
    public void handle(final Person person) {
        if (logger.isInfoEnabled()) {
            logger.info("MQ -> Sink: {}", person);
        }
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
}

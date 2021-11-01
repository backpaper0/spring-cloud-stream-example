package com.example;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.handler.LoggingHandler;

@Configuration
public class ConsumerFunction {

	private static Logger logger = LoggerFactory.getLogger(ConsumerFunction.class);

	/**
	 * RabbitMQから受信した内容を標準出力へ書き出す関数を定義しています。
	 * 
	 * @return
	 */
	@Bean
	public Consumer<Tweet> tweet() {
		return tweet -> {
			System.out.println("MQ -> consumer-service: " + tweet);
		};
	}

	@Autowired
	public void configureLoggingHandler(LoggingHandler handler) {
		logger.info("Set {} log level to {}", handler, LoggingHandler.Level.WARN);
		handler.setLevel(LoggingHandler.Level.WARN);
	}
}

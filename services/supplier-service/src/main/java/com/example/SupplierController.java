package com.example;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupplierController {

	private final StreamBridge streamBridge;

	public SupplierController(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void handleTweet(@RequestBody Tweet tweet) {
		System.out.println("supplier-service -> MQ: " + tweet);
		streamBridge.send("tweet", tweet);
	}

	@PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
	public void handleTweet(@RequestBody String tweet) {
		System.out.println("supplier-service -> MQ: " + tweet);
		streamBridge.send("tweet", tweet);
	}
}

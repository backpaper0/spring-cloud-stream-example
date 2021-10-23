package com.example;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Tweet {

	private final String content;

	public Tweet(@JsonProperty("content") String content) {
		this.content = Objects.requireNonNull(content);
	}

	public String getName() {
		return content;
	}

	@Override
	public String toString() {
		return content;
	}
}
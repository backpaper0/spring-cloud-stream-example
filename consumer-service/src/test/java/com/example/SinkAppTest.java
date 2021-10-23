package com.example;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
public class SinkAppTest {

	@Autowired
	private InputDestination input;

	@Test
	public void testHandle(CapturedOutput output) {
		final Message<String> message = new GenericMessage<>("{\"name\":\"hoge\"}");
		input.send(message);

		assertThat(output.getOut()).contains("MQ -> Sink: hoge");
	}
}
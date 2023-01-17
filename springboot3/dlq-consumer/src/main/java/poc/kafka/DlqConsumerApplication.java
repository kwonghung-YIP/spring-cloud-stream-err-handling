package poc.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
@SpringBootApplication
public class DlqConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlqConsumerApplication.class, args);
    }

    @Bean
    public Consumer<Message<Long>> deadletter() {
        return (msg) -> {
            MessageHeaders headers = msg.getHeaders();
            String dlqTopic = headers.get(KafkaHeaders.RECEIVED_TOPIC,String.class);
            String origTopic = new String(headers.get("x-original-topic",byte[].class), StandardCharsets.UTF_8);
            String exceptClass = new String(headers.get("x-exception-fqcn",byte[].class), StandardCharsets.UTF_8);
            String exceptMsg = new String(headers.get("x-exception-message",byte[].class), StandardCharsets.UTF_8);
            log.info("Received counter [{}] from topic [{}]",msg.getPayload(),dlqTopic);
        };
    }
}
